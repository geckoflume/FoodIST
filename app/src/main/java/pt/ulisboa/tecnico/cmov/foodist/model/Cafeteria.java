package pt.ulisboa.tecnico.cmov.foodist.model;

public class Cafeteria {
    private String name;
    private double latitude;
    private double longitude;
    private int campusId;

    public Cafeteria(String name, double latitude, double longitude, int campusId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.campusId = campusId;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getCampusId() {
        return campusId;
    }
}
