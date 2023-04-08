package algonquin.cst2335_yikaiwang_finalproject.nasaphotos.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import algonquin.cst2335_yikaiwang_finalproject.nasaphotos.model.NasaPhotoDetails;

@Dao
public interface NasaPhotoDAO {


    /**
     * Insert the information of the Nasa Mars photos into the database
     * <p>When user insert the same picture which have the same photo_id would be ignore
     * @param photo the DTO of {@link NasaPhotoDetails}
     * @return the record id
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long insert(algonquin.cst2335_yikaiwang_finalproject.nasaphotos.model.NasaPhotoDetails photo);

    /**
     * Retrieve all the {@link NasaPhotoDetails} from database
     * @return all the {@link NasaPhotoDetails}
     */
    @Query(
            "Select id, pexels_id, width, height, url, photographer, thumbnail, detailImg from NasaPhotos")
    public java.util.List<algonquin.cst2335_yikaiwang_finalproject.nasaphotos.model.NasaPhotoDetails> getAll();

    /**
     * Delete the specific {@link NasaPhotoDetails} in database
     * @param photo
     */
    @Delete
    public void delete(algonquin.cst2335_yikaiwang_finalproject.nasaphotos.model.NasaPhotoDetails photo);


}
