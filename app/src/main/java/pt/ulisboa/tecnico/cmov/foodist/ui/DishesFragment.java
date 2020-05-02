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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import pt.ulisboa.tecnico.cmov.foodist.PermissionsHelper;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.location.LocationUtils;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.DishViewModel;

public class DishesFragment extends Fragment {

    private DishViewModel mDishListViewModel;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private RecyclerView recyclerViewDish;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onViewCreated(container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_dishes, container, false);

        recyclerViewDish = root.findViewById(R.id.recyclerView_dishes); //TODO
        DishAdapter adapterDishes = new DishAdapter();
        recyclerViewDish.setAdapter(adapterDishes);

        mDishListViewModel = new ViewModelProvider(requireActivity()).get(DishViewModel.class);

        // Add an observer on the LiveData returned by getCafeterias.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the cafeterias in the adapter.
        mDishListViewModel.getDish().observe(getViewLifecycleOwner(), adapterDishes::setDishList);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> ((CafeteriaActivity) getActivity()).updateDish());
        mDishListViewModel.isUpdating().observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing); // TODO

        return root;
    }
/*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setIndoorEnabled(true);
        if (PermissionsHelper.checkPermissions(getActivity())) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        // Set observer
        mCafeteriaListViewModel.getCafeterias().observe(getViewLifecycleOwner(), cafeteriaEntities ->
                LocationUtils.updateMap(this.mMap, this.mapFragment, cafeteriaEntities));
        /*
        mMap.setOnMarkerClickListener(marker -> {
            recyclerViewCafeterias.scrollToPosition(pos); // TODO: compute pos
            return true;
        });
         */
    //}
}
