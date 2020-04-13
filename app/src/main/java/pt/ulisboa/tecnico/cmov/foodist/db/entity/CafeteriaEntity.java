package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;

@Entity(tableName = "cafeterias")
public class CafeteriaEntity implements Cafeteria {
    @PrimaryKey
    @ColumnInfo(index = true)
    private int id;
    @NonNull
    private String name;
    @NonNull
    private double latitude;
    @NonNull
    private double longitude;
    @NonNull
    private double distance = 0.;
    @NonNull
    @SerializedName("time_walk")
    @ColumnInfo(name = "time_walk")
    private double timeWalk = 0.;
    @NonNull
    @SerializedName("time_wait")
    @ColumnInfo(name = "time_wait")
    private double timeWait = 0.;
    @NonNull
    @SerializedName("campus_id")
    @ColumnInfo(name = "campus_id")
    private int campusId;

    public CafeteriaEntity() {
    }

    @Ignore
    public CafeteriaEntity(@NonNull String name, @NonNull double latitude, @NonNull double longitude, @NonNull int campusId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.campusId = campusId;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int getCampusId() {
        return campusId;
    }

    @Override
    public double getTimeWait() {
        return timeWait;
    }

    @Override
    public void setTimeWait(double time) {
        this.timeWait = time;
    }

    @Override
    public double getTimeWalk() {
        return timeWalk;
    }

    @Override
    public void setTimeWalk(double time) {
        this.timeWalk = time;
    }

    public void setCampusId(int campusId) {
        this.campusId = campusId;
    }
}
