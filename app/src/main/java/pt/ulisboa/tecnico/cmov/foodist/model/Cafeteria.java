package pt.ulisboa.tecnico.cmov.foodist.model;

import androidx.annotation.NonNull;

public interface Cafeteria {
    int getId();
    void setId(int id);

    String getName();
    void setName(String name);

    double getLatitude();
    void setLatitude(double latitude);

    double getLongitude();
    void setLongitude(double longitude);

    int getDistance();
    void setDistance(int distance);

    int getCampusId();
    void setCampusId(int campusId);

    int getTimeWait();
    void setTimeWait(int time);

    int getTimeWalk();
    void setTimeWalk(int time);
}
