package pt.ulisboa.tecnico.cmov.foodist.location;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the Directions API response, according to
 * https://developers.google.com/maps/documentation/directions/intro
 */
public class DataParser {
    public List<LatLng> parse(JSONObject jObject) {
        List<LatLng> path = new ArrayList<>();
        JSONArray jRoute;
        JSONArray jLeg;
        JSONArray jSteps;
        String polyline;
        List<LatLng> decodedPolyline;
        int duration = 0;

        try {
            jRoute = jObject.getJSONArray("routes");
            // Getting first route
            jLeg = ((JSONObject) jRoute.get(0)).getJSONArray("legs");
            // Getting first leg
            jSteps = ((JSONObject) jLeg.get(0)).getJSONArray("steps");
            // Traversing all steps
            for (int i = 0; i < jSteps.length(); i++) {
                polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(i)).get("polyline")).get("points");
                decodedPolyline = decodePoly(polyline);

                path.addAll(decodedPolyline);
                duration += (Integer) ((JSONObject) ((JSONObject) jSteps.get(i)).get("duration")).get("value");
            }
            Log.i("Dataparser", "Total duration in s: " + duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
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
}