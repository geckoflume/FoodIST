package pt.ulisboa.tecnico.cmov.foodist.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Picture {
    @Expose(serialize = false)
    private int id;

    @Expose
    @SerializedName("dish_id")
    private int dishId;

    @Expose
    private String filename;
}
