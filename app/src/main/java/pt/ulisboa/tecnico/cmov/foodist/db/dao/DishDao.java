package pt.ulisboa.tecnico.cmov.foodist.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;

@Dao
public interface DishDao {

    @Query("SELECT * FROM dishes WHERE cafeteria_id = :cafeteriaId")
    LiveData<List<DishEntity>> getAllByCafeteriaId(int cafeteriaId);

    @Query("SELECT * FROM dishes WHERE id = :id LIMIT 1")
    LiveData<DishEntity> findById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<DishEntity> cafeterias);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DishEntity dish);

    @Query("DELETE FROM dishes WHERE cafeteria_id = :cafeteriaId")
    void deleteAllByCafeteriaId(int cafeteriaId);

    @Delete
    void delete(DishEntity dish);
}
