package algonquin.cst2335_yikaiwang_finalproject.nasaphotos.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {algonquin.cst2335_yikaiwang_finalproject.nasaphotos.model.NasaPhotoDetails.class},
        version = 1
)
public abstract class NasaPhotoDatabase extends RoomDatabase {

    /**
     * Get the DAO object {@link NasaPhotoDAO}
     *
     * @return an instance of the DAO object {@link NasaPhotoDAO}
     */
    public abstract NasaPhotoDAO nasaPhotoDao();
}
