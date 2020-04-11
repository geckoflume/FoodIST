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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public int getCampusId() {
        return campusId;
    }

    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCampusId(int campusId) {
        this.campusId = campusId;
    }
}
