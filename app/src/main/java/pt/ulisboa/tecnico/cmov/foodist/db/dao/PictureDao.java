package pt.ulisboa.tecnico.cmov.foodist.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.PictureEntity;

@Dao
public interface PictureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PictureEntity picture);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PictureEntity> pictures);

    @Delete
    void delete(PictureEntity picture);

    @Delete
    void deleteAll(List<PictureEntity> pictures);

    @Query("SELECT * FROM pictures WHERE dish_id = :dishId LIMIT 1")
    List<PictureEntity> getAllByDishIdEntities(int dishId);
}