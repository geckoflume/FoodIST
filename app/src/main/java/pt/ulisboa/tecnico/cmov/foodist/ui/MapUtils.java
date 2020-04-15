package pt.ulisboa.tecnico.cmov.foodist.ui;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;

public class MapUtils {
    public static MarkerOptions createMarker(Cafeteria cafeteria) {
        return new MarkerOptions()
                .position(new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude()))
                .title(cafeteria.getName());
    }
}
