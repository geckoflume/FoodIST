package pt.ulisboa.tecnico.cmov.foodist.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.PictureEntity;

@Dao
public interface PictureDao {
    @Query("SELECT * FROM pictures WHERE dish_id = :dishId")
    LiveData<List<PictureEntity>> getAllByDishId(int dishId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PictureEntity picture);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PictureEntity> pictures);
}