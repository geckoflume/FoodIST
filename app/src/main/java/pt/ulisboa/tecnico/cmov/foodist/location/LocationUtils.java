package pt.ulisboa.tecnico.cmov.foodist.location;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;

public class LocationUtils {
    public static float calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[3];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0];
    }

    public static MarkerOptions createMarker(Cafeteria cafeteria) {
        return new MarkerOptions()
                .position(new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude()))
                .title(cafeteria.getName());
    }

    public static void updateMap(GoogleMap map, SupportMapFragment mapFragment, List<? extends Cafeteria> cafeteriasList) {
        if (cafeteriasList.size() == 1) {
            updateMap(map, cafeteriasList.get(0));
        } else if (!cafeteriasList.isEmpty() && map != null) {
            CameraUpdate cameraUpdate;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            map.clear();
            for (Cafeteria cafeteria : cafeteriasList) {
                // Add each cafeteria on the map and in the LatLngBounds builder
                map.addMarker(LocationUtils.createMarker(cafeteria));
                builder.include(new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude()));
            }
            LatLngBounds bounds = builder.build();
            int height = mapFragment.getView().getHeight();
            // offset from edges of the map 15% of screen height
            int padding = (int) (height * 0.15);
            cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            map.moveCamera(cameraUpdate);  // or use animateCamera() for smooth animation
        }
    }

    public static MarkerOptions updateMap(GoogleMap map, Cafeteria cafeteria) {
        CameraUpdate cameraUpdate;

        // Workaround for "bizarre" zoom level when only one marker
        MarkerOptions marker = LocationUtils.createMarker(cafeteria);
        map.addMarker(marker);
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude()), 17F);

        map.moveCamera(cameraUpdate);  // or use animateCamera() for smooth animation
        return marker;
    }
}
