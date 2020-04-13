package pt.ulisboa.tecnico.cmov.foodist.model;

public interface Cafeteria {
    int getId();

    String getName();

    double getLatitude();

    double getLongitude();

    double getDistance();

    void setDistance(double distance);

    int getCampusId();

    double getTimeWait();

    void setTimeWait(double time);

    double getTimeWalk();

    void setTimeWalk(double time);
}
