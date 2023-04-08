package algonquin.cst2335_yikaiwang_finalproject.nasaphotos;

import android.lin_project.R.id;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.data.NasaPhotoViewModel;
import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.model.NasaPhotoDetails;

public class NasaPhotoRowHolder extends ViewHolder {

    /** The textview of Nasa Mars photo */
    TextView textViewNasaPhoto;
    /** The imageview of the Nasa Mars photo */
    ImageView imageViewNasaPhoto;
    /** The data of the {@link NasaPhotoDetails} */
    NasaPhotoDetails photo;

    /**
     * The constructor
     *
     * @param itemView the layout of item
     * @param viewModel the view model where data stored
     */
    public NasaPhotoRowHolder(@NonNull View itemView, NasaPhotoViewModel viewModel) {
        super(itemView);
        // init the UI element
        imageViewNasaPhoto = itemView.findViewById(id.imageViewNasaPhoto);
        textViewNasaPhoto = itemView.findViewById(id.textViewNasaPhoto);
        // add item view click event handler
        itemView.setOnClickListener(
                clk -> {
                    // before set photo we have to set rowHolder first as there's an observer on photos
                    viewModel.getSelectedRowHolder().setValue(this);
                    viewModel.getSelectedPhoto().setValue(photo);
                });
    }

    public NasaPhotoRowHolder(@NonNull View itemView) {
        super(itemView);
    }
}
