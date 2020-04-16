package pt.ulisboa.tecnico.cmov.foodist.location;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class DirectionsFetcher {
    private URL url;

    public DirectionsFetcher(Context context, LatLng l1, LatLng l2) throws MalformedURLException {
        String strUrl = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + l1.latitude + ","
                + l1.longitude + "&destination="
                + l2.latitude + ","
                + l2.longitude + "&mode=walking&key="
                + context.getString(R.string.google_maps_key);
        this.url = new URL(strUrl);
    }

    public String fetchDirections() {
        String response = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            // Creating an http connection
            urlConnection = (HttpURLConnection) this.url.openConnection();
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
