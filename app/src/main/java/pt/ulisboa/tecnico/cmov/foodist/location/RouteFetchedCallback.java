package pt.ulisboa.tecnico.cmov.foodist.location;

import com.google.android.gms.maps.model.PolylineOptions;

public interface RouteFetchedCallback {
    void onRouteFetched(PolylineOptions polyline);
}
