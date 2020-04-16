package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
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
    private int distance = 0;

    @NonNull
    @SerializedName("time_walk")
    @ColumnInfo(name = "time_walk")
    private int timeWalk = 0;

    @NonNull
    @SerializedName("time_wait")
    @ColumnInfo(name = "time_wait")
    private int timeWait = 0;

    @NonNull
    @SerializedName("campus_id")
    @ColumnInfo(name = "campus_id")
    private int campusId;

    public CafeteriaEntity() {
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int getDistance() {
        return distance;
    }

    @Override
    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public int getCampusId() {
        return campusId;
    }

    @Override
    public void setCampusId(int campusId) {
        this.campusId = campusId;
    }

    @Override
    public int getTimeWait() {
        return timeWait;
    }

    @Override
    public void setTimeWait(int time) {
        this.timeWait = time;
    }

    @Override
    public int getTimeWalk() {
        return timeWalk;
    }

    @Override
    public void setTimeWalk(int time) {
        this.timeWalk = time;
    }
}
