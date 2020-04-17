package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.PermissionsHelper;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ActivityCafeteriaBinding;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.location.LocationUtils;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaViewModel;

public class CafeteriaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = CafeteriaActivity.class.getSimpleName();
    private Toolbar toolbar;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private CafeteriaViewModel cafeteriaViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private CafeteriaEntity currentCafeteria;
    private MutableLiveData<Boolean> cafeteriaWasInitialized = new MutableLiveData<>(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCafeteriaBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_cafeteria);

        // Get the Intent that started this activity and extract the string
        String message = getIntent().getStringExtra(CafeteriaAdapter.EXTRA_MESSAGE);

        CafeteriaViewModel.Factory factory = new CafeteriaViewModel.Factory(getApplication(), Integer.parseInt(message));
        cafeteriaViewModel = new ViewModelProvider(this, factory).get(CafeteriaViewModel.class);

        binding.setLifecycleOwner(this);                        // important to observe LiveData!!
        binding.setCafeteriaViewModel(cafeteriaViewModel);
        initActionBar();

        cafeteriaViewModel.getCafeteria().observe(this, cafeteriaEntity -> {
            currentCafeteria = cafeteriaEntity;
            if (!cafeteriaWasInitialized.getValue())
                cafeteriaWasInitialized.setValue(true);
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDetail);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().liteMode(true));     // for lite mode
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapDetail, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setIndoorEnabled(true);
        if (PermissionsHelper.checkPermissions(this)) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            updateMap();
        }
    }

    private void updateMap() {
        cafeteriaWasInitialized.observe(this, shouldUpdateMap -> {
            if (shouldUpdateMap) {                                      // should only happen once
                Log.w(TAG, "Cafeteria view model got observed, now updating the map");
                LocationUtils.updateMap(mMap, currentCafeteria);
                fusedLocationClient.getLastLocation().addOnSuccessListener(mCurrentLocation ->
                        ((BasicApp) getApplication()).networkIO().execute(() -> {
                            List<LatLng> path = cafeteriaViewModel.updateCafeteriaDistance(currentCafeteria,
                                    mCurrentLocation, getString(R.string.google_maps_key));
                            if (path != null && !path.isEmpty()) { // ensures a route has been found and that the provided Google Maps api key is valid
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng point : path)
                                    builder.include(point);
                                LatLngBounds bounds = builder.build();
                                int height = mapFragment.getView().getHeight();
                                // offset from edges of the map 10% of screen height
                                int padding = (int) (height * 0.1);

                                @SuppressLint("ResourceType")
                                PolylineOptions polyline = new PolylineOptions()
                                        .addAll(path)
                                        .width(20)
                                        .color(Color.parseColor(getString(R.color.colorBlueGoogleMaps)));

                                mapFragment.getView().post(() -> { // run this on main thread
                                    mMap.addPolyline(polyline);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));  // or use animateCamera() for smooth animation
                                });
                            }
                        }));
            }
        });
    }

    public void openDirections(View view) {
        // This uri does not straight open navigation on Google Maps
        // "?q=" query parameter needed for Google Maps
        // https://developers.google.com/maps/documentation/urls/android-intents#location_search
        String urlNavigation = "geo:0,0?q="
                + currentCafeteria.getLatitude() + "," + currentCafeteria.getLongitude();

        // This uri does but is not handled by every mapping app
        /*
        String urlNavigation = "http://maps.google.com/maps?&daddr="
                + cafeteriaEntity.getLatitude() + ","
                + cafeteriaEntity.getLongitude();
         */
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlNavigation)));
    }

    public void addMeal(View view) {
        Intent intent = new Intent(this, newMeal.class);
        intent.putExtra("IdCafet", currentCafeteria.getId());
        startActivity(intent);
    }
}
