package pt.ulisboa.tecnico.cmov.foodist.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.DishEntity;

@Dao
public interface DishDao {

    @Query("SELECT * FROM dishes")
    LiveData<List<DishEntity>> getAll();
/*
    @Query("SELECT * FROM cafeterias WHERE id LIKE :id LIMIT 1")
    LiveData<DishEntity> findById(int id);
*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DishEntity> dishes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DishEntity dish);

    /*
    @Update
    void updateAll(List<DishEntity> cafeterias);

    @Update
    void update(DishEntity cafeteria);

*/

}
