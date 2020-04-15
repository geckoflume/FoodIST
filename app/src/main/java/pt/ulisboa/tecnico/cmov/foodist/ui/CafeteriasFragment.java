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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaListViewModel;

public class CafeteriasFragment extends Fragment implements OnMapReadyCallback {

    private CafeteriaListViewModel mCafeteriaListViewModel;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private RecyclerView recyclerViewCafeterias;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onViewCreated(container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_cafeterias, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().liteMode(true));     // for lite mode
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        recyclerViewCafeterias = root.findViewById(R.id.recyclerView_cafeterias);
        CafeteriaAdapter adapterCafeterias = new CafeteriaAdapter();
        recyclerViewCafeterias.setAdapter(adapterCafeterias);

        mCafeteriaListViewModel = new ViewModelProvider(requireActivity()).get(CafeteriaListViewModel.class);

        // Add an observer on the LiveData returned by getCafeterias.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the cafeterias in the adapter.
        mCafeteriaListViewModel.getCafeterias().observe(getViewLifecycleOwner(), adapterCafeterias::setCafeteriaList);
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setIndoorEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Set observer
        mCafeteriaListViewModel.getCafeterias().observe(getViewLifecycleOwner(), cafeteriaEntities -> MapUtils.updateMap(this.mMap, this.mapFragment, cafeteriaEntities));
        /*
        mMap.setOnMarkerClickListener(marker -> {
            recyclerViewCafeterias.scrollToPosition(pos); // TODO: compute pos
            return true;
        });
         */
    }
}
