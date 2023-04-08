package algonquin.cst2335_yikaiwang_finalproject.nasaphotos;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.lin_project.databinding.FragmentNasaPhotoDetailBinding;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.data.NasaPhotoDAO;
import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.data.NasaPhotoViewModel;
import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.model.NasaPhotoDetails;

/**
 * Fragment to replace the frame layout
 * @author Yikai Wang
 * @version 1.0
 * @since Apr 2023
 */
public class NasaPhotoFragment extends Fragment {

    /** details to hold all parameters */
    private NasaPhotoDetails nasaPhotoDetails;

    /** fragment view model */
    private NasaPhotoViewModel nasaPhotoViewModel;

    /** prefs to store information */
    private SharedPreferences prefs;

    /** to hold a single row */
    ViewHolder soccerGameRowHolder;

    /** The adapter of the RecyclerView */
    private Adapter<NasaPhotoRowHolder> fragmentAdapter;

    /** single thread pull */
    private Executor thread = Executors.newSingleThreadExecutor();

    /** DAO */
    NasaPhotoDAO nasaPhotoDAO;

    /** view binding */
    FragmentNasaPhotoDetailBinding binding;
    private android.view.LayoutInflater inflater;


    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     *
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, add a {@link LifecycleObserver} on the
     * activity's Lifecycle, removing it when it receives the
     * {@link State#CREATED} callback.
     *
     * <p>Any restored child fragments will be created before the base
     * <code>Fragment.onCreate</code> method returns.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (prefs.getString("url", "null").length() != 0 ) {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(prefs.getString("url", null)));
                startActivity(viewIntent);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between
     * {@link #onCreate(Bundle)} and {@link #onViewCreated(View, Bundle)}.
     * <p>A default View can be returned by calling  in your
     * constructor. Otherwise, this method returns null.
     *
     * <p>It is recommended to <strong>only</strong> inflate the layout in this method and move
     * logic that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentNasaPhotoDetailBinding.inflate(inflater);

        binding.cameraName.setText(android.lin_project.R.string.camera_name);
        binding.cameraName.setText(
                binding.cameraName.getText().toString() + nasaPhotoDetails.getCamera());

        String nasaPhotoUrl = nasaPhotoDetails.getPhotoUrl();
        binding.nasaPhotoUrl.setText(nasaPhotoUrl);

        // ImageUtils.loadNetworkImage(
        // binding.imageViewDetail, photoDetail, false, appCompatActivity.getApplicationContext());
        // pexelsPicture.setDetailImg(photoDetail);

        // add backToList button click event handler
        binding.btnBackToPrevious.setOnClickListener(clk -> requireActivity().onBackPressed());
        // init the visible of the delete button
        if (requireActivity() instanceof PexelsDbActivity) {
            binding.btnAddToFavourite.setVisibility(View.GONE);

        } else {
            binding.btnDelete.setVisibility(View.GONE);
        }

        binding.btnDelete.setEnabled(null != pexelsViewModel.getSelectedPhoto().getValue());

        // add AddToFavourite button click event handler
        binding.btnAddToFavourite.setOnClickListener(
                clk ->
                        thread.execute(
                                () -> {
                                    pexelsPictureDAO.insert(pexelsPicture);
                                    com.google.android.material.snackbar.Snackbar.make(this.getView(), android.lin_project.R.string.pexels_add_photo, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                                            .show();
                                }));
        // add Delete button click event handler
        binding.btnDelete.setOnClickListener(
                clk -> {
                    if (null == pexelsViewModel.getSelectedPhoto()) {
                        binding.btnDelete.setEnabled(false);
                        return;
                    }
                    int pos = pexelsPictureRowHolder.getAbsoluteAdapterPosition();
                    new AlertDialog.Builder(appCompatActivity)
                            .setTitle("Question:")
                            .setMessage("Do you want to delete this record?")
                            .setNegativeButton("No", (dialog, cL) -> {})
                            .setPositiveButton(
                                    "Yes",
                                    (dialog, cl) -> {
                                        thread.execute(() -> pexelsPictureDAO.delete(pexelsPicture));
                                        pexelsPictureAdapter.notifyItemRemoved(pos);
                                        pexelsViewModel.getPexelsPhotos().getValue().remove(pos);
                                        pexelsViewModel.getPreviousDelPhoto().setValue(pexelsPicture);
                                        pexelsViewModel.getSelectedPhoto().setValue(null);
                                        binding.btnDelete.setEnabled(false);
                                        com.google.android.material.snackbar.Snackbar.make(
                                                        this.getView(), "You deleted record #" + pos, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                                                .setAction(
                                                        "Undo",
                                                        clk1 -> {
                                                            thread.execute(() -> pexelsPictureDAO.insert(pexelsPicture));
                                                            pexelsViewModel.getPexelsPhotos().getValue().add(pos, pexelsPicture);
                                                            pexelsViewModel.getSelectedPhoto().setValue(pexelsPicture);
                                                            pexelsViewModel.getPreviousDelPhoto().setValue(null);
                                                            pexelsPictureAdapter.notifyItemInserted(pos);
                                                            binding.btnDelete.setEnabled(true);
                                                        })
                                                .show();
                                    })
                            .create()
                            .show();
                });
        // Inflate the layout for this fragment
        View view = binding.getRoot();
        view.setClickable(true);
        return view;
    }

}
