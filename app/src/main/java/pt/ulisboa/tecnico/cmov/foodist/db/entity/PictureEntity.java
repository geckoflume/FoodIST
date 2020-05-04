package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pt.ulisboa.tecnico.cmov.foodist.model.Picture;

@Entity(tableName = "pictures")
public class PictureEntity implements Picture {
    @Expose(serialize = false)
    @PrimaryKey
    @ColumnInfo(index = true)
    private int id;

    @Expose
    @SerializedName("dish_id")
    @ColumnInfo(name = "dish_id")
    private int dishId;

    @NonNull
    @Expose
    private String filename = "";

    public PictureEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    @NonNull
    public String getFilename() {
        return filename;
    }

    public void setFilename(@NonNull String filename) {
        this.filename = filename;
    }
}
