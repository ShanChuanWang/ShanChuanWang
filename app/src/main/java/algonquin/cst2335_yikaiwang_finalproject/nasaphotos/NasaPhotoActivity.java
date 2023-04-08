package algonquin.cst2335_yikaiwang_finalproject.nasaphotos;

import static java.net.URLEncoder.encode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.lin_project.R;
import android.lin_project.R.id;
import android.lin_project.R.string;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.room.Room;
import androidx.viewbinding.ViewBinding;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.data.NasaPhotoDAO;
import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.data.NasaPhotoDatabase;
import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.data.NasaPhotoViewModel;
import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.model.NasaPhotoDetails;


public abstract class NasaPhotoActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    /** The key "NasaPhotoDetails" in the {@link SharedPreferences} */
    protected static final String DATA_SET_KEY = "Nasa Mars Photo";

    /** The sub key "date" in the {@link SharedPreferences} {@value DATA_SET_KEY} */
    // protected static final String THEDATE  = "date";

    private ActivityNasaPhotoBinding binding;

    /** For store value the user previously entered */
    protected SharedPreferences prefs;

    /** Store data to continue displaying data after the app is rotated */
    protected NasaPhotoViewModel nasaPhotoViewModel;

    /** In order to show the retrieve data from Nasa Mars photos */
    protected Adapter<NasaPhotoRowHolder> nasaPhotoAdapter;

    /** The DAO object */
    protected NasaPhotoDAO nasaPhotoDAO;

    /** A single thread for data manipulation */
    protected Executor thread = Executors.newSingleThreadExecutor();

    /** The {@link AlertDialog} will be showed during the data retrieve */
    protected AlertDialog loadingDataAlertDialog;

    /** The row holder for Recycler view */
    private NasaPhotoRowHolder nasaPhotoRowHolder;

    /**
     * Build the ViewModel for the current activity
     *
     * @return the instance of {@link NasaPhotoViewModel}
     */
    public NasaPhotoViewModel buildViewModel() {
        return new ViewModelProvider(this).get(NasaPhotoViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // build view model
        nasaPhotoViewModel = buildViewModel();
        // init database
        NasaPhotoDatabase db =
                Room.databaseBuilder(
                                getApplicationContext(), NasaPhotoDatabase.class, "Nasa Mars Photos")
                        .build();
        // init DAO object
        nasaPhotoDAO = db.nasaPhotoDao();
        nasaPhotoViewModel.getNasaPhotoDAOLive().setValue(nasaPhotoDAO);
        // init AlertDialog for data loading
        loadingDataAlertDialog = getLoadingDataAlertDialog();
        //
        nasaPhotoAdapter = buildAdapter();
        nasaPhotoViewModel.getNasaPhotoAdapterLive().setValue(buildAdapter());
        //
        prefs = getSharedPreferences(DATA_SET_KEY, Context.MODE_PRIVATE);
        //
        ViewBinding binding = getActivityBinding();

        // Initialize RequestQueue
        VolleySingleton.getInstance(this).getRequestQueue();
        setContentView(binding.getRoot());
        /*
        if (binding instanceof ActivityNasaDbBinding) {
            ActivityNasaDbBinding dbBinding = (ActivityNasaDbBinding) binding;
            setupRecyclerView(dbBinding.recyclerView);
            setupToolbar(dbBinding.mytoolbar, dbBinding.pexelsDrawerLayout, dbBinding.nvPexelsMenu);
        }
        if (binding instanceof ActivityNasaOnlineBinding) {
            ActivityPexelsOnlineBinding onlineBinding = (ActivityPexelsOnlineBinding) binding;
            setupRecyclerView(onlineBinding.recyclerView);
            setupToolbar(
                    onlineBinding.mytoolbar, onlineBinding.pexelsDrawerLayout, onlineBinding.nvPexelsMenu);
        }
        */
        setupModelObserverListener();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment detailFragment = supportFragmentManager.findFragmentById(id.fragmentLocation);
        FragmentTransaction ft = supportFragmentManager.beginTransaction();

        // Remove the previous one
        if (null != detailFragment) {
            ft.remove(detailFragment);
            ft.commitAllowingStateLoss();
        }
        super.onSaveInstanceState(out);
    }

    /** Setup the data listener by using observer design pattern */
    private void setupModelObserverListener() {
        // Use fragment to show the details
        nasaPhotoViewModel
                .getSelectedPhoto()
                .observe(
                        this,
                        (photo) -> {
                            nasaPhotoViewModel.getAppCompatActivity().setValue(this);
                            NasaPhotoFragment fragment = new NasaPhotoFragment();
                            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                            /*
                             * Reference
                             * Fragment manager
                             * https://developer.android.com/guide/fragments/fragmentmanager
                             */
                            tx.replace(id.fragmentLocation, fragment)
                                    // in order to enable back to activity
                                    .addToBackStack(null)
                                    .commit();
                        });

        // close the alert dialog when the list size changed
        nasaPhotoViewModel
                .getNasaPhotos()
                .observe(
                        this,
                        (photos) -> {
                            if (null != loadingDataAlertDialog) {
                                loadingDataAlertDialog.dismiss();
                            }
                        });

        // Show the alertdialog when error occurred
        nasaPhotoViewModel
                .getErrorMsg()
                .observe(
                        this,
                        (errMsg -> {
                            if (null != loadingDataAlertDialog) {
                                loadingDataAlertDialog.dismiss();
                            }
                            new Builder(this)
                                    .setTitle("Error:")
                                    .setMessage(errMsg)
                                    .setPositiveButton(
                                            "Ok",
                                            (dialog, clickOk) -> {
                                                // do nothing
                                            })
                                    .create()
                                    .show();
                        }));
    }

    /**
     * Ask the subclass to offer their {@link ViewBinding}
     *
     * @return the subclass's {@link ViewBinding}
     */
    public abstract ViewBinding getActivityBinding();

    /** Setup the recycler view ui and load the data */
    protected void setupRecyclerView(RecyclerView recyclerView) {
        // load data from view model
        ArrayList<NasaPhotoDetails> data =
                null == nasaPhotoViewModel.getNasaPhotos().getValue()
                        ? new ArrayList<>()
                        : nasaPhotoViewModel.getNasaPhotos().getValue();

        if (0 == data.size()) {
            nasaPhotoViewModel.getNasaPhotos().setValue(data);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(nasaPhotoAdapter);
    }

    /**
     * Build a data adapter for RecyclerView
     *
     * @return a data adapter
     */
    private Adapter<NasaPhotoRowHolder> buildAdapter() {
        return new Adapter<NasaPhotoRowHolder>() {
            /**
             * This function creates a ViewHolder object It represents a single row's layout in the list
             *
             * @param parent
             * @param viewType
             * @return An instance of {@link NasaPhotoRowHolder}
             */
            @NonNull
            @Override
            public NasaPhotoRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d("Adapter", "onCreateViewHolder==>");
                View root = LayoutNasaPhotoBinding.inflate(getLayoutInflater()).getRoot();
                nasaPhotoRowHolder = new NasaPhotoRowHolder(root, nasaPhotoViewModel);
                return nasaPhotoRowHolder;
            }

            /**
             * This initializes a ViewHolder to go at the row specified by the position parameter.
             *
             * @param holder
             * @param position
             * @see <a href=https://stackoverflow.com/a/58516290/19790100>How to Download file in internal
             *     Storage using Volley in Android Studio</a>
             */
            @Override
            public void onBindViewHolder(@NonNull NasaPhotoRowHolder holder, int position) {

                NasaPhotoDetails nasaPhotoDetails = nasaPhotoViewModel.getNasaPhotos().getValue().get(position);
                Log.d("Adapter", "onBindViewHolder==>" + position + ", The photo is =>" + nasaPhotoDetails);
                holder.textViewPhotographer.setText(nasaPhotoDetails.getPhotographer());
                /*
                 * Reference
                 * Canvas: trying to draw too large bitmap" when Android N Display Size set larger than Small
                 * https://stackoverflow.com/questions/39111248/canvas-trying-to-draw-too-large-bitmap-when-android-n-display-size-set-larger
                 * https://developer.android.com/topic/performance/hardware-accel
                 * */
                String thumbnailUrl = nasaPhotoDetails.getThumbnail();
                String thumbnail =
                        null == thumbnailUrl || "".equals(thumbnailUrl)
                                ? nasaPhotoDetails.getSrc().getTiny()
                                : thumbnailUrl;
                ImageUtils.loadNetworkImage(
                        holder.imageViewPhoto, thumbnail, true, getApplicationContext());
                nasaPhotoDetails.setThumbnail(thumbnail);
                holder.photo = nasaPhotoDetails;
            }

            /**
             * This function just returns an int specifying how many items to draw.
             *
             * @return the size of the data
             */
            @Override
            public int getItemCount() {
                ArrayList<NasaPhotoDetails> value = nasaPhotoViewModel.getNasaPhotos().getValue();
                return null == value ? 0 : value.size();
            }
        };
    }

    /**
     * Prepare a {@link AlertDialog} for data loading
     * @return An instance of {@link AlertDialog}
     */
    protected AlertDialog getLoadingDataAlertDialog() {
        // ProgressBar is shown when loading data from server
        final ProgressBar progressBar = new ProgressBar(this.getApplicationContext());
        return new Builder(this)
                .setTitle("Retrieving Data")
                .setMessage("Loading data, this may take a few seconds...")
                .setView(progressBar)
                .create();
    }

    /** This is the welcome message */
    protected void welcomeMessage() {
        new Builder(this)
                .setMessage(string.welcome_message)
                .setPositiveButton(
                        "Ok",
                        (dialog, clickOk) -> {
                            // do nothing
                        })
                .create()
                .show();
    }

    protected void setUpNavigationDrawer() {
        setSupportActionBar(binding.nasa_photo_toolbar);
    }

    /**
     * Setup the toolbar
     * @param toolbar
     * @param drawer
     * @param navigationView
     */
    protected void setupToolbar(Toolbar toolbar, DrawerLayout drawer, NavigationView navigationView) {
        // load the toolbar
        setSupportActionBar(toolbar);
        // Use the hamburger button to show the menu, so we can jump to different Activity
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this, drawer, toolbar, string.pexels_open, string.pexels_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(
                (item) -> {
                    onOptionsItemSelected(item);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_nasa_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case id.goto_weather_stack:
                // todo change the class name to weatherStack
                intent = new Intent(NasaPhotoActivity.this, NasaPhotoFavouriteActivity.class);
                break;
            case id.goto_place_kitten:
                // todo change the class name to Place Kitten
                intent = new Intent(NasaPhotoActivity.this, NasaPhotoFavouriteActivity.class);
                break;
            case id.goto_ny_times:
                // todo change the class name to New York Times
                intent = new Intent(NasaPhotoActivity.this, NasaPhotoFavouriteActivity.class);
                break;
            default:
                intent = new Intent(NasaPhotoActivity.this, NasaPhotoFavouriteActivity.class);
                break;
        }
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }



    protected void dataRetrievedUsingAPI() {

    }

    private void loadDataFromApi() {
        String url = null;
        try {
            url = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol="
                    + encode(String.valueOf(binding.theDate.getText()), "UTF-8")
                    + "&api_key=krgVJA5ybba5NvcWlkkyBcF1sQVTUPHdTfVrHyqC";
            JsonArrayRequest request =
                    new JsonArrayRequest(Method.GET, url, null,
                            (response) -> {
                                try {
                                    ArrayList<NasaPhotoDetails> nasaPhotoList = new ArrayList<>();
                                    for (int i = 0; i < Math.min(response.length(), 20); i++) {
                                        JSONObject photoObject = response.getJSONObject(i);
                                        JSONObject camera = photoObject.getJSONObject("camera");
                                        String cameraName = camera.getString("full_name");
                                        String photoUrl = photoObject.getString("img_src");
                                        NasaPhotoDetails nasaPhotoDetails = new NasaPhotoDetails(cameraName, photoUrl);
                                        nasaPhotoList.add(nasaPhotoDetails);

                                    }
                                    if (0 == nasaPhotoList.size()) {
                                        nasaPhotoAdapter.notifyItemRangeRemoved(
                                                0, nasaPhotoViewModel.getNasaPhotos().getValue().size());
                                    } else {
                                        nasaPhotoAdapter.notifyItemRangeChanged(0, nasaPhotoList.size());
                                    }
                                    nasaPhotoViewModel.getNasaPhotos().setValue(
                                            nasaPhotoList);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            new ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Nasa Mars Photos", error.getMessage());
                                }
                            });
            VolleySingleton.getInstance(NasaPhotoActivity.this)
                    .getRequestQueue()
                    .add(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        }

}
