package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaListViewModel;

public class CafeteriasFragment extends Fragment implements OnMapReadyCallback {

    private CafeteriaListViewModel mCafeteriaListViewModel;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onViewCreated(container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_cafeterias, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        RecyclerView recyclerViewCafeterias = root.findViewById(R.id.recyclerView_cafeterias);
        CafeteriaAdapter adapterCafeterias = new CafeteriaAdapter();
        recyclerViewCafeterias.setAdapter(adapterCafeterias);
        mCafeteriaListViewModel = new ViewModelProvider(requireActivity()).get(CafeteriaListViewModel.class);

        // Add an observer on the LiveData returned by getCafeterias.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the cafeterias in the adapter.
        mCafeteriaListViewModel.getCafeterias().observe(getViewLifecycleOwner(), adapterCafeterias::setCafeteriaList);
        mCafeteriaListViewModel.getCafeterias().observe(getViewLifecycleOwner(), this::updateMap);
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void updateMap(List<? extends Cafeteria> cafeteriasList) {
        CameraUpdate cameraUpdate;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (mMap != null) {
            mMap.clear();

            if (cafeteriasList.size() == 1) {
                // Workaround for "bizarre" zoom level when only one marker
                Cafeteria cafeteria = cafeteriasList.get(0);
                mMap.addMarker(createMarker(cafeteria));
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude()), 17F);
            } else {
                for (Cafeteria cafeteria : cafeteriasList) {
                    mMap.addMarker(createMarker(cafeteria));
                    builder.include(new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude()));
                }
                LatLngBounds bounds = builder.build();
                int height = mapFragment.getView().getHeight();
                // offset from edges of the map 15% of screen height
                int padding = (int) (height * 0.15);
                cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            }
            mMap.moveCamera(cameraUpdate);  // or use animateCamera() for smooth animation
        }
    }

    private MarkerOptions createMarker(Cafeteria cafeteria) {
        return new MarkerOptions()
                .position(new LatLng(cafeteria.getLatitude(), cafeteria.getLongitude()))
                .title(cafeteria.getName());
    }
}
