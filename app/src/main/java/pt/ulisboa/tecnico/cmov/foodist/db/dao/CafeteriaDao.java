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
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaPartialEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaWithOpeningHours;

@Dao
public interface CafeteriaDao {
    @Query("SELECT * FROM cafeterias ORDER BY distance")
    LiveData<List<CafeteriaEntity>> getAll();

    @Query("SELECT * FROM cafeterias WHERE campus_id = :campusId ORDER BY distance")
    LiveData<List<CafeteriaEntity>> getAllByCampusId(int campusId);

    @Query("SELECT * FROM cafeterias ORDER BY distance")
    List<CafeteriaEntity> getCafeteriasEntities();

    @Query("SELECT * FROM cafeterias WHERE campus_id = :campusId ORDER BY distance")
    List<CafeteriaEntity> getAllByCampusIdEntities(int campusId);

    @Query("SELECT * FROM cafeterias WHERE id = :id LIMIT 1")
    LiveData<CafeteriaEntity> getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CafeteriaEntity> cafeterias);

    @Update
    void updateAll(List<CafeteriaEntity> cafeterias);

    @Update(entity = CafeteriaEntity.class)
    void updateAllPartial(List<CafeteriaPartialEntity> cafeteriasPartial);

    @Update
    void update(CafeteriaEntity cafeteria);

    @Update(entity = CafeteriaEntity.class)
    void updatePartial(CafeteriaPartialEntity cafeteria);

    @Transaction
    @Query("SELECT cafeterias.* FROM cafeterias JOIN openinghours ON openinghours.cafeteria_id = cafeterias.id WHERE openinghours.status = :status GROUP BY cafeterias.id")
    LiveData<List<CafeteriaWithOpeningHours>> getCafeteriasWithOpeningHours(int status);

    @Transaction
    @Query("SELECT cafeterias.* FROM cafeterias JOIN openinghours ON openinghours.cafeteria_id = cafeterias.id WHERE cafeterias.campus_id = :campusId AND openinghours.status = :status GROUP BY cafeterias.id")
    LiveData<List<CafeteriaWithOpeningHours>> getCafeteriasWithOpeningHoursByCampusId(int status, int campusId);

    @Query("SELECT cafeterias.* FROM cafeterias, dishes WHERE cafeterias.id=dishes.cafeteria_id AND dishes.id = :dishId LIMIT 1")
    LiveData<CafeteriaEntity> getByIdDish(int dishId);
}
