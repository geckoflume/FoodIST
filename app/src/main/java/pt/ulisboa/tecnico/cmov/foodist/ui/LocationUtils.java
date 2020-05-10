package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;

public abstract class LocationUtils {
    public static float calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[3];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0];
    }

    public static MarkerOptions createMarker(CafeteriaEntity cafeteria) {
        return new MarkerOptions()
                .position(new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude()))
                .title(cafeteria.getName());
    }

    public static void updateMap(GoogleMap map, SupportMapFragment mapFragment, List<? extends CafeteriaEntity> cafeteriasList) {
        if (cafeteriasList.size() == 1) {
            updateMap(map, cafeteriasList.get(0));
        } else if (!cafeteriasList.isEmpty() && map != null) {
            CameraUpdate cameraUpdate;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            map.clear();
            for (CafeteriaEntity cafeteria : cafeteriasList) {
                // Add each cafeteria on the map and in the LatLngBounds builder
                map.addMarker(LocationUtils.createMarker(cafeteria));
                builder.include(new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude()));
            }
            LatLngBounds bounds = builder.build();
            bounds = expandBounds(bounds);
            int height = mapFragment.getView().getHeight();
            // offset from edges of the map 10% of screen height
            int padding = (int) (height * 0.1);
            cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            map.moveCamera(cameraUpdate);
        }
    }

    /**
     * Method to expand the bounds to fit the markers heights
     *
     * @param bounds
     * @return
     */
    private static LatLngBounds expandBounds(LatLngBounds bounds) {
        double paddingTop = (bounds.northeast.latitude - bounds.southwest.latitude) * 0.2;
        return bounds.including(new LatLng(bounds.northeast.latitude + paddingTop, bounds.northeast.longitude));
    }

    public static MarkerOptions updateMap(GoogleMap map, CafeteriaEntity cafeteria) {
        CameraUpdate cameraUpdate;

        // Workaround for "bizarre" zoom level when only one marker
        MarkerOptions marker = LocationUtils.createMarker(cafeteria);
        map.addMarker(marker);
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude()), 17F);

        map.animateCamera(cameraUpdate);
        return marker;
    }
}
