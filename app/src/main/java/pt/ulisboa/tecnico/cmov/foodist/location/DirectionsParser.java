package pt.ulisboa.tecnico.cmov.foodist.location;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the Directions API response, according to
 * https://developers.google.com/maps/documentation/directions/intro
 */
public class DirectionsParser {
    private static final String TAG = DirectionsParser.class.getSimpleName();
    private List<LatLng> path;
    private int distance;
    private int duration;

    public DirectionsParser(String jsonStr, LatLng origin, LatLng dest) {
        this.path = new ArrayList<>();
        this.duration = 0;
        this.distance = 0;
        JSONObject jStep;
        String polyline;

        this.path.add(origin);

        try {
            JSONObject jObject = new JSONObject(jsonStr);
            JSONArray jRoute = jObject.getJSONArray("routes");
            // Getting first route if it exists
            if (jRoute.length() != 0) {
                JSONArray jLeg = jRoute.getJSONObject(0).getJSONArray("legs");
                // Getting first leg
                JSONArray jSteps = jLeg.getJSONObject(0).getJSONArray("steps");
                // Traversing all steps
                for (int i = 0; i < jSteps.length(); i++) {
                    jStep = jSteps.getJSONObject(i);
                    polyline = jStep.getJSONObject("polyline").getString("points");
                    this.path.addAll(decodePoly(polyline));
                    this.duration += jStep.getJSONObject("duration").getInt("value");
                    this.distance += jStep.getJSONObject("distance").getInt("value");
                }
            } else
                Log.e(TAG, "Could not find a route, wrong location or API key?");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.path.add(dest);
    }

    /**
     * Method to decode polyline points
     *
     * Courtesy of Jeffrey Sambells:
     * http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public List<LatLng> getPath() {
        return path;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}