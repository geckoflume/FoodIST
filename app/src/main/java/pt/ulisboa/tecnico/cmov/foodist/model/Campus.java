package pt.ulisboa.tecnico.cmov.foodist.model;

import android.location.Location;
import android.util.Log;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.location.LocationUtils;

public class Campus {
    private static final String TAG = Campus.class.getSimpleName();
    private int id;
    private String name;
    private double latitude;
    private double longitude;

    public Campus(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
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

    @Override
    public String toString() {
        return name;
    }

    /**
     * Find and returns the nearest campus
     */
    public static Campus findNearest(List<Campus> campuses, Location location) {
        Campus nearest = null;
        double distanceNearest = 0.f;
        double distanceCandidate;

        for (Campus c : campuses) {
            if (nearest == null) {
                nearest = c;
                distanceNearest = LocationUtils.calculateDistance(location.getLatitude(), location.getLongitude(), nearest.getLatitude(), nearest.getLongitude());
            } else {
                distanceCandidate = LocationUtils.calculateDistance(location.getLatitude(), location.getLongitude(), c.getLatitude(), c.getLongitude());
                if (distanceCandidate < distanceNearest) {
                    nearest = c;
                    distanceNearest = LocationUtils.calculateDistance(location.getLatitude(), location.getLongitude(), nearest.getLatitude(), nearest.getLongitude());
                }
            }
        }
        Log.i(TAG, "Nearest campus is " + nearest + " at " + distanceNearest + "m.");
        return nearest;
    }
}
