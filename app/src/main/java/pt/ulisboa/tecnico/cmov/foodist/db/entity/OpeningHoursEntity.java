package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.TextStyle;

import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.model.OpeningHours;

@Entity(tableName = "openinghours")
public class OpeningHoursEntity implements OpeningHours {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    private int id;

    @NonNull
    @SerializedName("day_of_week")
    @ColumnInfo(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @NonNull
    @SerializedName("from_time")
    @ColumnInfo(name = "from_time")
    private LocalTime fromTime;

    @NonNull
    @SerializedName("to_time")
    @ColumnInfo(name = "to_time")
    private LocalTime toTime;

    @NonNull
    @SerializedName("cafeteria_id")
    @ColumnInfo(name = "cafeteria_id")
    private int cafeteriaId;

    @NonNull
    private int status;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public LocalTime getFromTime() {
        return fromTime;
    }

    @Override
    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    @Override
    public LocalTime getToTime() {
        return toTime;
    }

    @Override
    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }

    @Override
    public int getCafeteriaId() {
        return cafeteriaId;
    }

    @Override
    public void setCafeteriaId(int cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isOpen() {
        LocalTime now = LocalTime.now();
        return now.isAfter(fromTime) && now.isBefore(toTime);
    }

    @Override
    public String toString() {
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()) + "\t " + fromTime + " - " + toTime;
    }
}
