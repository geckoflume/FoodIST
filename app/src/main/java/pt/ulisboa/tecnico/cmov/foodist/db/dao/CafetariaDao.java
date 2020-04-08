package pt.ulisboa.tecnico.cmov.foodist.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;

@Dao
public interface CafetariaDao {
    @Query("SELECT * FROM cafeterias")
    LiveData<List<CafeteriaEntity>> getAll();

    @Query("SELECT * FROM cafeterias WHERE campus_id = :campusId ORDER BY distance")
    LiveData<List<CafeteriaEntity>> getAllByCampusId(int campusId);

    @Query("SELECT * FROM cafeterias WHERE id LIKE :id LIMIT 1")
    LiveData<CafeteriaEntity> findById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CafeteriaEntity> cafeterias);
}
