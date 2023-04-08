package algonquin.cst2335_yikaiwang_finalproject.nasaphotos;

import android.lin_project.databinding.ActivityNasaPhotoFavouriteBinding;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;

import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.model.NasaPhotoDetails;

public class NasaPhotoFavouriteActivity extends NasaPhotoActivity{

    @Override
    public ViewBinding getActivityBinding() {
        return ActivityNasaPhotoFavouriteBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveData();
    }

    /** Retrieve data from database */
    private void retrieveData() {
        loadingDataAlertDialog.show();
        thread.execute(
                () -> {
                    ArrayList<NasaPhotoDetails> all = (ArrayList<NasaPhotoDetails>) nasaPhotoDAO.getAll();
                    runOnUiThread(
                            () -> {
                                nasaPhotoViewModel.getNasaPhotos().getValue();
                                nasaPhotoAdapter.notifyItemChanged(0,all.size());
                            }
                    );
                }
        );
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
