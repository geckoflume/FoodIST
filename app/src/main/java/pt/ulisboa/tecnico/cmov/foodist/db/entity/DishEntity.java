package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @Expose
    private boolean haveInfo;

    @Expose
    private boolean haveMeat;

    @Expose
    private boolean haveFish;

    @Expose
    private boolean isVegetarian;

    @Expose
    private boolean isVegan;

    @Expose
    @NonNull
    private String data ="";

    public DishEntity() {
    }

    @Ignore
    public DishEntity(String name, double price, int cafeteriaId) {
        this.name = name;
        this.price = price;
        this.cafeteriaId = cafeteriaId;

        this.haveInfo = false;
        this.haveMeat = false;
        this.haveFish = false;
        this.isVegan = false;
        this.isVegetarian = false;
        this.data = "No information";
    }

    @Ignore
    public DishEntity(String name, double price, int cafeteriaId, String info) {
        this.name = name;
        this.price = price;
        this.cafeteriaId = cafeteriaId;

        this.haveInfo = false;
        this.haveMeat = false;
        this.haveFish = false;
        this.isVegan = false;
        this.isVegetarian = false;
        this.data = info;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
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
    public boolean getHaveInfo(){
        return haveInfo;
    }

    @Override
    public void setHaveInfo(boolean haveInfo){
        this.haveInfo = haveInfo;
    }

    @Override
    public boolean getHaveMeat(){
        return haveMeat;
    }

    @Override
    public void setHaveMeat(boolean haveMeat) {
        this.haveMeat = haveMeat;
    }

    @Override
    public boolean getHaveFish() {
        return haveFish;
    }

    @Override
    public void setHaveFish(boolean haveFish) {
        this.haveFish = haveFish;
    }

    @Override
    public boolean getIsVegetarian() {
        return isVegetarian;
    }

    @Override
    public void setIsVegetarian(boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    @Override
    public boolean getIsVegan() {
        return isVegan;
    }

    @Override
    public void setIsVegan(boolean isVegan) {
        this.isVegan = isVegan;
    }

    @Override
    public String getData(){
        return data;
    }

    @Override
    public void setData(String data){
        this.data = data;
    }
    public void setDatas(boolean meat, boolean fish, boolean vege, boolean vegan){
        setHaveFish(fish);
        setHaveMeat(meat);
        setIsVegan(vegan);
        setIsVegetarian(vege);
        setHaveInfo(true);
        this.data = " This is :";
        if (meat) { data = data + "Meat "; }
        if (fish) { data = data + "Fish "; }
        if (vege) { data = data + "Vegetarian "; }
        if (vegan) { data = data + "Vegan "; }
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

