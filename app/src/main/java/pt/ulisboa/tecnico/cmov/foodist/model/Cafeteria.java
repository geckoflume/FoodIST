package pt.ulisboa.tecnico.cmov.foodist.model;

public interface Cafeteria {
    int getId();

    String getName();

    double getLatitude();

    double getLongitude();

    double getDistance();

    void setDistance(double distance);

    int getCampusId();
}
