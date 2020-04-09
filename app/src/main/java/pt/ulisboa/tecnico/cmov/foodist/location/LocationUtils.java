package pt.ulisboa.tecnico.cmov.foodist.location;

import android.location.Location;

public class LocationUtils {
    public static float calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[3];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0];
    }
}
