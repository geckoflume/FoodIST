package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ActivityCafeteriaBinding;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaViewModel;

public class CafeteriaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar toolbar;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private CafeteriaViewModel cafeteriaViewModel;

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
        cafeteriaViewModel.getCafeteria().observe(mapFragment.getViewLifecycleOwner(), cafeteriaEntity -> MapUtils.updateMap(this.mMap, cafeteriaEntity));
    }

}
