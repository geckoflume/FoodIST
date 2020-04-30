package pt.ulisboa.tecnico.cmov.foodist.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;

public class DirectionsFetcher {
    private final String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    private final String urlString;
    private String response;
    private final LatLng origin;
    private final LatLng destination;

    public DirectionsFetcher(final String apiKey, final CafeteriaEntity cafeteria, final Location location) {
        this.origin = new LatLng(location.getLatitude(), location.getLongitude());
        this.destination = new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude());

        urlString = baseUrl + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&mode=walking&key=" + apiKey; // context.getString(R.string.google_maps_key);
        fetchDirections();
    }

    private void fetchDirections() {
        String response = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            // Creating an http connection
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading response from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            response = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (iStream != null)
                    iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        this.response = response;
    }

    public DirectionsParser parse() {
        return new DirectionsParser(response, origin, destination);
    }
}
