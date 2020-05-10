package pt.ulisboa.tecnico.cmov.foodist.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;

@Entity
public class CafeteriaPartialEntity implements Cafeteria {
    private int id;

    @SerializedName("wait_time")
    @ColumnInfo(name = "wait_time")
    private int timeWait = 0;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getTimeWait() {
        return timeWait;
    }

    @Override
    public void setTimeWait(int time) {
        this.timeWait = time;
    }
}
