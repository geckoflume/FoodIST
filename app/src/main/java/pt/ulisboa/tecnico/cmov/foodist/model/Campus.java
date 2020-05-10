package pt.ulisboa.tecnico.cmov.foodist.model;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.ui.LocationUtils;

public class Campus {
    private static final String TAG = Campus.class.getSimpleName();
    public static final int AUTODETECT = 0;
    public static final int ALL = 1;
    public static final int DEFAULT = 1;
    public static final int FIRST_CAMPUS = 2;
    public static final int DISTANCE_THRESHOLD = 1000; // if the campus is more than 1km away, we ignore it
    private static ArrayList<Campus> campusList = null;

    private int id;
    private String name;
    private double latitude;
    private double longitude;

    private Campus(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static ArrayList<Campus> getInstance(Context context) {
        // Singleton
        if (campusList == null) {
            campusList = new ArrayList<>();
            campusList.add(new Campus(0, context.getString(R.string.find_nearest), 0, 0));              // 0
            campusList.add(new Campus(1, context.getString(R.string.all_cafeterias), 0, 0));            // 1
            campusList.add(new Campus(2, context.getString(R.string.alameda), 38.736795, -9.138637));   // 2
            campusList.add(new Campus(3, context.getString(R.string.taguspark), 38.737461, -9.303161)); // 3
            campusList.add(new Campus(4, context.getString(R.string.ctn), 38.811911, -9.094221));       // 4
        }
        return campusList;
    }

    public int getId() {
        return id;
    }

    private double getLatitude() {
        return latitude;
    }

    private double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Find and returns the nearest campus
     */
    public static Campus findNearest(Context context, Location location) {
        List<Campus> campuses = getInstance(context);
        Campus nearest = campuses.get(DEFAULT);
        Campus candidate;
        double distanceNearest = LocationUtils.calculateDistance(location.getLatitude(), location.getLongitude(), nearest.getLatitude(), nearest.getLongitude());
        double distanceCandidate;

        for (int i = FIRST_CAMPUS; i < campuses.size(); i++) {
            candidate = campuses.get(i);
            distanceCandidate = LocationUtils.calculateDistance(location.getLatitude(), location.getLongitude(), candidate.getLatitude(), candidate.getLongitude());
            if (distanceCandidate < DISTANCE_THRESHOLD && distanceCandidate < distanceNearest) {
                nearest = candidate;
                distanceNearest = LocationUtils.calculateDistance(location.getLatitude(), location.getLongitude(), nearest.getLatitude(), nearest.getLongitude());
            }
        }
        Log.d(TAG, "Nearest campus is " + nearest + " at " + distanceNearest + "m.");
        return nearest;
    }
}
