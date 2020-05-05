package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pt.ulisboa.tecnico.cmov.foodist.model.Dish;

@Entity(tableName = "dishes")
public class DishEntity implements Dish {
    @Expose(serialize = false)
    @PrimaryKey
    @ColumnInfo(index = true)
    private int id;

    @Expose
    @NonNull
    private String name = "";

    @Expose
    private double price;

    @Expose
    @SerializedName("cafeteria_id")
    @ColumnInfo(name = "cafeteria_id")
    private int cafeteriaId;

    public DishEntity() {
    }

    @Ignore
    public DishEntity(String name, double price, int cafeteriaId) {
        this.name = name;
        this.price = price;
        this.cafeteriaId = cafeteriaId;
    }

    @Override
    public void setCafeteriaId(int cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }

    @Override
    public int getCafeteriaId() {
        return cafeteriaId;
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
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public double getPrice() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Dish)) return false;
        DishEntity that = (DishEntity) o;
        return id == that.id &&
                Double.compare(that.price, price) == 0 &&
                cafeteriaId == that.cafeteriaId &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + name.hashCode();
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + cafeteriaId;
        return result;
    }
}

