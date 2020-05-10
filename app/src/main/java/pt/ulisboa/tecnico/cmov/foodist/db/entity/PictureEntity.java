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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getDishId() {
        return dishId;
    }

    @Override
    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    @NonNull
    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(@NonNull String filename) {
        this.filename = filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Picture)) return false;
        PictureEntity that = (PictureEntity) o;
        return id == that.id &&
                dishId == that.dishId &&
                filename.equals(that.filename);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + dishId;
        result = 31 * result + filename.hashCode();
        return result;
    }
}
