package pt.ulisboa.tecnico.cmov.foodist.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaWithOpeningHours;

@Dao
public interface CafeteriaDao {
    @Query("SELECT * FROM cafeterias ORDER BY distance")
    LiveData<List<CafeteriaEntity>> getAll();

    @Query("SELECT * FROM cafeterias WHERE campus_id = :campusId ORDER BY distance")
    LiveData<List<CafeteriaEntity>> getAllByCampusId(int campusId);

    @Query("SELECT * FROM cafeterias WHERE id LIKE :id LIMIT 1")
    LiveData<CafeteriaEntity> findById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CafeteriaEntity> cafeterias);

    @Update
    void updateAll(List<CafeteriaEntity> cafeterias);

    @Update
    void update(CafeteriaEntity cafeteria);

    @Transaction
    @Query("SELECT * FROM cafeterias")
    LiveData<List<CafeteriaWithOpeningHours>> getCafeteriasWithOpeningHours();

    @Transaction
    @Query("SELECT * FROM cafeterias WHERE id LIKE :id LIMIT 1")
    LiveData<List<CafeteriaWithOpeningHours>> getCafeteriaWithOpeningHours(int id);
}
