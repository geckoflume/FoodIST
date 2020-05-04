package pt.ulisboa.tecnico.cmov.foodist.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.OpeningHoursEntity;

@Dao
public interface OpeningHoursDao {
    @Query("SELECT * FROM openinghours WHERE cafeteria_id = :cafeteriaId AND status = :status ORDER BY day_of_week, from_time")
    LiveData<List<OpeningHoursEntity>> getAllByCafeteriaStatus(int cafeteriaId, int status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<OpeningHoursEntity> openingHours);
}