package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import pt.ulisboa.tecnico.cmov.foodist.model.Dish;

@Entity(tableName = "dishes")
public class DishEntity implements Dish {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String price;

    @NonNull
    @SerializedName("cafet_id")
    @ColumnInfo(name = "cafet_id")
    private int cafetId;

    public DishEntity() {
    }

    public DishEntity(String n, String p, int i) {
        this.name = n;
        this.price = p;
        this.cafetId = i;
    }

    @Override
    public void setCafetId(int cafetId) {
        this.cafetId = cafetId;
    }

    @Override
    public int getCafetId() {
        return cafetId;
    }

    @Override
    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setPrice(@NonNull String price) {
        this.price = price;
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int i) {
        id = i;
    }
}

