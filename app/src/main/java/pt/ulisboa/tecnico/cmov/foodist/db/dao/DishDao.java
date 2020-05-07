package pt.ulisboa.tecnico.cmov.foodist.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishWithPictures;

@Dao
public interface DishDao {
    @Query("SELECT * FROM dishes WHERE cafeteria_id = :cafeteriaId")
    List<DishEntity> getAllByCafeteriaIdEntities(int cafeteriaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<DishEntity> cafeterias);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DishEntity dish);

    @Query("DELETE FROM dishes WHERE id = :dishId")
    void deleteById(int dishId);

    @Delete
    void deleteAll(List<DishEntity> dishes);

    @Transaction
    @Query("SELECT * FROM dishes WHERE cafeteria_id = :cafeteriaId")
    LiveData<List<DishWithPictures>> getDishesWithPicturesByCafeteriaId(int cafeteriaId);

    @Transaction
    @Query("SELECT * FROM dishes WHERE id = :id LIMIT 1")
    LiveData<DishWithPictures> getDishWithPictures(int id);

    @Query("SELECT * FROM dishes WHERE id = :id LIMIT 1")
    DishEntity getDishByIdEntity(int id);
}
