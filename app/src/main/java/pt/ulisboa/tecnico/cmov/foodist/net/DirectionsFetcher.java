package pt.ulisboa.tecnico.cmov.foodist.net;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;

public class DirectionsFetcher {
    private final String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    public String response;
    private final LatLng origin;
    private final LatLng destination;

    public DirectionsFetcher(final String apiKey, final CafeteriaEntity cafeteria, final Location location) {
        this.origin = new LatLng(location.getLatitude(), location.getLongitude());
        this.destination = new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude());

        String urlString = baseUrl + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&mode=walking&key=" + apiKey; // context.getString(R.string.google_maps_key);
        this.response = NetUtils.get(urlString, 200);
    }

    public String getResponse() {
        return response;
    }

    public DirectionsParser parse() {
        if (response != null && !response.isEmpty())
            return new DirectionsParser(response, origin, destination);
        else return null;
    }
}
