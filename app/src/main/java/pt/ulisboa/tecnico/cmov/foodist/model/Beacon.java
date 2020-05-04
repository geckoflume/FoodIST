package pt.ulisboa.tecnico.cmov.foodist.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

public class Beacon {
    @Expose(serialize = false)
    private int id;

    @Expose
    @SerializedName("cafeteria_id")
    private int cafeteriaId;

    @Expose
    @SerializedName("datetime_arrive")
    private String datetimeArrive;

    @Expose(serialize = false)
    @SerializedName("datetime_leave")
    private String datetimeLeave;

    public Beacon(int cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
        this.datetimeArrive = OffsetDateTime.now(ZoneOffset.UTC).toString();
    }

    public void setDatetimeLeaveNow() {
        this.datetimeLeave = OffsetDateTime.now(ZoneOffset.UTC).toString();
    }

    public int getId() {
        return id;
    }
}
