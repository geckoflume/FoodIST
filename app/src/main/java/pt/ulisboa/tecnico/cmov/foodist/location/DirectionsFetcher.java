package pt.ulisboa.tecnico.cmov.foodist.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.net.NetUtils;

public class DirectionsFetcher {
    private final String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    private String response;
    private final LatLng origin;
    private final LatLng destination;

    public DirectionsFetcher(final String apiKey, final CafeteriaEntity cafeteria, final Location location) {
        this.origin = new LatLng(location.getLatitude(), location.getLongitude());
        this.destination = new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude());

        String urlString = baseUrl + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&mode=walking&key=" + apiKey; // context.getString(R.string.google_maps_key);
        this.response = NetUtils.download(urlString);
    }

    public DirectionsParser parse() {
        return new DirectionsParser(response, origin, destination);
    }
}
