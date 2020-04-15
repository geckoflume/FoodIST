package pt.ulisboa.tecnico.cmov.foodist.model;

import com.google.android.gms.maps.model.PolylineOptions;

public interface Cafeteria {
    int getId();

    String getName();

    double getLatitude();

    double getLongitude();

    int getDistance();

    void setDistance(int distance);

    int getCampusId();

    void setCampusId(int campusId);

    int getTimeWait();

    void setTimeWait(int time);

    int getTimeWalk();

    void setTimeWalk(int time);
}
