package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.net.MalformedURLException;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ActivityCafeteriaBinding;
import pt.ulisboa.tecnico.cmov.foodist.location.DirectionsFetcher;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.location.DirectionsParser;
import pt.ulisboa.tecnico.cmov.foodist.location.LocationUtils;
import pt.ulisboa.tecnico.cmov.foodist.model.Cafeteria;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaViewModel;

public class CafeteriaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar toolbar;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private CafeteriaViewModel cafeteriaViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private int cafeteriaId;

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
        mMap.setMyLocationEnabled(true);
        cafeteriaViewModel.getCafeteria().observe(
                mapFragment.getViewLifecycleOwner(),
                cafeteriaEntity -> {
                    if (cafeteriaId != cafeteriaEntity.getId()) {
                        cafeteriaId = cafeteriaEntity.getId();
                        this.updateMap(this.mMap, cafeteriaEntity);
                    }
                });
    }

    private void updateMap(GoogleMap map, Cafeteria cafeteria) {
        MarkerOptions markerOptions = LocationUtils.updateMap(map, cafeteria);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null && mapFragment.getView() != null) {
                // Logic to handle location object
                ((BasicApp) CafeteriaActivity.this.getApplication()).networkIO().execute(() -> {
                    try {
                        DirectionsFetcher directionsFetcher = new DirectionsFetcher(
                                mapFragment.getView().getContext(),
                                markerOptions.getPosition(),
                                new LatLng(location.getLatitude(),
                                        location.getLongitude()));
                        String response = directionsFetcher.fetchDirections();
                        DirectionsParser directionsParser = new DirectionsParser(response);
                        cafeteriaViewModel.updateCafeteriaDirections(
                                directionsParser.getDistance(),
                                directionsParser.getDuration());

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng point : directionsParser.getPath()) {
                            builder.include(point);
                        }
                        LatLngBounds bounds = builder.build();
                        int height = mapFragment.getView().getHeight();
                        // offset from edges of the map 10% of screen height
                        int padding = (int) (height * 0.10);

                        if (mapFragment.getView() != null) {
                            PolylineOptions polyline = new PolylineOptions()
                                    .addAll(directionsParser.getPath())
                                    .width(20)
                                    .color(Color.BLUE);

                            mapFragment.getView().post(() -> {
                                mMap.addPolyline(polyline);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));  // or use animateCamera() for smooth animation
                            });
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public void addMeal(View view) {
        Intent intent = new Intent(this, newMeal.class);
        CafeteriaEntity[] myCafeteria = new CafeteriaEntity[1];
        cafeteriaViewModel.getCafeteria().observe(this, new Observer<CafeteriaEntity>() { // Send the cafeteria information
            @Override
            public void onChanged(CafeteriaEntity cafet){
                myCafeteria[0] = cafet;
            }
        });
        intent.putExtra("IdCafet", myCafeteria[0].getId());
        startActivity(intent);

    }
}
