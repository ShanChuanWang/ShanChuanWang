package algonquin.cst2335_yikaiwang_finalproject.nasaphotos.data;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;

import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.NasaPhotoRowHolder;
import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.model.NasaPhotoDetails;

public class NasaPhotoViewModel extends ViewModel {

    /** The list of {@link NasaPhotoDetails} grab from online website or database */
    private final MutableLiveData<ArrayList<NasaPhotoDetails>> nasaPhotos = new MutableLiveData<>();

    /** The {@link NasaPhotoDetails} that user selected */
    private final MutableLiveData<NasaPhotoDetails> selectedPhoto = new MutableLiveData<>();

    /** The {@link NasaPhotoDetails} that user previous deleted */
    private final MutableLiveData<NasaPhotoDetails> previousDelPhoto = new MutableLiveData<>();

    /** The {@link NasaPhotoRowHolder} that represent the selected Nasa Mars photo */
    private final MutableLiveData<NasaPhotoViewModel> selectedRowHolder = new MutableLiveData<>();

    /** The error message */
    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();

    /** The {@link NasaPhotoDAO} DAO object that manipulate the data */
    private final MutableLiveData<NasaPhotoDAO> nasaPhotoDAOLive = new MutableLiveData<>();

    /** This store the activity which the fragment will use */
    private final MutableLiveData<AppCompatActivity> appCompatActivity = new MutableLiveData<>();

    /** The adapter for RecyclerView */
    private final MutableLiveData<Adapter<NasaPhotoRowHolder>>
            nasaPhotoAdapterLive = new MutableLiveData<>();

    public MutableLiveData<ArrayList<NasaPhotoDetails>> getNasaPhotos() {
        return nasaPhotos;
    }

    public MutableLiveData<NasaPhotoDetails> getSelectedPhoto() {
        return selectedPhoto;
    }

    public MutableLiveData<NasaPhotoDetails> getPreviousDelPhoto() {
        return previousDelPhoto;
    }

    public MutableLiveData<NasaPhotoViewModel> getSelectedRowHolder() {
        return selectedRowHolder;
    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public MutableLiveData<NasaPhotoDAO> getNasaPhotoDAOLive() {
        return nasaPhotoDAOLive;
    }

    public MutableLiveData<AppCompatActivity> getAppCompatActivity() {
        return appCompatActivity;
    }

    public MutableLiveData<Adapter<NasaPhotoRowHolder>> getNasaPhotoAdapterLive() {
        return nasaPhotoAdapterLive;
    }
}
