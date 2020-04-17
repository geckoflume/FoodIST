package pt.ulisboa.tecnico.cmov.foodist.location;

import android.location.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;

public class DirectionsFetcher {
    private final String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    private String urlString;

    public DirectionsFetcher(String apiKey, CafeteriaEntity cafeteria, Location location) {
        urlString = baseUrl + cafeteria.getLatitude() + "," + cafeteria.getLongitude()
                + "&destination=" + location.getLatitude() + "," + location.getLongitude()
                + "&mode=walking&key=" + apiKey; // context.getString(R.string.google_maps_key);
    }

    public String fetchDirections() {
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
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return response;
    }
}
