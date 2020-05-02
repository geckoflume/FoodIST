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
    private String name = "";

    private double latitude;

    private double longitude;

    private int distance = 0;

    @SerializedName("walk_time")
    @ColumnInfo(name = "walk_time")
    private int timeWalk = 0;

    @SerializedName("wait_time")
    @ColumnInfo(name = "wait_time")
    private int timeWait = 0;

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

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCampusId() {
        return campusId;
    }

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

    public int getTimeWalk() {
        return timeWalk;
    }

    public void setTimeWalk(int time) {
        this.timeWalk = time;
    }
}
