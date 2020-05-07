package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.Locale;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.PermissionsHelper;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.databinding.ActivityCafeteriaBinding;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.OpeningHoursEntity;
import pt.ulisboa.tecnico.cmov.foodist.model.Status;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaViewModel;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.DishListViewModel;

public class CafeteriaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = CafeteriaActivity.class.getSimpleName();
    private Toolbar toolbar;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private CafeteriaViewModel cafeteriaViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private CafeteriaEntity currentCafeteria;
    private MutableLiveData<Boolean> updateRequest = new MutableLiveData<>(false);
    private DishListViewModel dishListViewModel;
    private boolean isCheckBoxRouteTicked = false;
    private Locale currentLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCafeteriaBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_cafeteria);

        // Get the Intent that started this activity and extract the string
        String message = getIntent().getStringExtra(CafeteriaAdapter.EXTRA_MESSAGE);

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int statusId = sharedPref.getInt("status", Status.DEFAULT);
        CafeteriaViewModel.Factory factory = new CafeteriaViewModel.Factory(getApplication(), Integer.parseInt(message), statusId);
        cafeteriaViewModel = new ViewModelProvider(this, factory).get(CafeteriaViewModel.class);

        binding.setLifecycleOwner(this);                        // important to observe LiveData!!
        binding.setCafeteriaViewModel(cafeteriaViewModel);
        initActionBar();

        currentLocale = getResources().getConfiguration().locale;

        cafeteriaViewModel.getCafeteria().observe(this, cafeteriaEntity -> {
            currentCafeteria = cafeteriaEntity;
            if (!updateRequest.getValue())
                updateRequest.setValue(true);
        });

        cafeteriaViewModel.getOpeningHours().observe(this, openingHoursEntities -> {
            StringBuilder days = new StringBuilder();
            StringBuilder hours = new StringBuilder();
            OpeningHoursEntity current;
            for (int i = 0; i < openingHoursEntities.size(); i++) {
                current = openingHoursEntities.get(i);
                if (i == 0 || current.getDayOfWeek() != openingHoursEntities.get(i - 1).getDayOfWeek())
                    days.append(current.dayToString(currentLocale));
                hours.append(current.timesToString());
                if (openingHoursEntities.size() != i + 1) {
                    days.append("\n");
                    hours.append("\n");
                }
            }
            cafeteriaViewModel.updateOpenHoursTextDays(days.toString());
            cafeteriaViewModel.updateOpenHoursTextHours(hours.toString());
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

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefresh_cafeteria);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        cafeteriaViewModel.isUpdating().observe(this, swipeRefreshLayout::setRefreshing);
        swipeRefreshLayout.setOnRefreshListener(() -> updateRequest.setValue(true));


        CheckBox repeatChkBx = findViewById(R.id.checkBox_route);
        repeatChkBx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isCheckBoxRouteTicked = isChecked;
            updateRequest.setValue(true);
        });

        //Get all dishes by cafeteriaId
        DishListViewModel.Factory factoryDishes = new DishListViewModel.Factory(getApplication(), Integer.parseInt(message));
        dishListViewModel = new ViewModelProvider(this, factoryDishes).get(DishListViewModel.class);

        RecyclerView recyclerViewDishes = this.findViewById(R.id.recyclerView_dishes);
        DishAdapter adapterDishes = new DishAdapter();
        recyclerViewDishes.setAdapter(adapterDishes);
        dishListViewModel.getDishesWithPictures().observe(this, adapterDishes::setDishList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cafeteria_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_cafeteria:
                shareCafeteria();
                break;
            case R.id.action_refresh:
                updateRequest.setValue(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareCafeteria() {
        String shareText = UiUtils.formatShareCafeteria(
                cafeteriaViewModel.getCafeteria().getValue(),
                getString(R.string.share_cafeteria_text));
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setIndoorEnabled(true);
        if (PermissionsHelper.checkPermissionLocation(this)) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            updateRequest.observe(this, shouldUpdateMap -> {
                if (shouldUpdateMap) {
                    Log.w(TAG, "Cafeteria view model got observed, now updating the map");
                    cafeteriaViewModel.setUpdating(true);
                    mMap.clear();
                    LocationUtils.updateMap(mMap, currentCafeteria);
                    ((BasicApp) getApplication()).networkIO().execute(() -> {
                        cafeteriaViewModel.updateCafeteriaWaitTime();
                        dishListViewModel.updateDishes();
                        dishListViewModel.updateFirstPicture();
                        if (!isCheckBoxRouteTicked)
                            cafeteriaViewModel.setUpdating(false);
                    });
                    if (isCheckBoxRouteTicked) {
                        fusedLocationClient.getLastLocation().addOnSuccessListener(mCurrentLocation ->
                                ((BasicApp) getApplication()).networkIO().execute(() -> {
                                    List<LatLng> path = cafeteriaViewModel.updateCafeteriaDistance(currentCafeteria, mCurrentLocation, getString(R.string.google_cloud_key));
                                    if (path != null && !path.isEmpty()) { // ensures a route has been found and that the provided Google Maps api key is valid
                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        for (LatLng point : path)
                                            builder.include(point);
                                        LatLngBounds bounds = builder.build();
                                        int height = mapFragment.getView().getHeight();
                                        // offset from edges of the map 10% of screen height
                                        int padding = (int) (height * 0.1);

                                        PolylineOptions polyline = new PolylineOptions()
                                                .addAll(path)
                                                .width(20)
                                                .color(ResourcesCompat.getColor(getResources(), R.color.colorBlueGoogleMaps, null)); // more elegant than Color.parseColor(getString(R.color.colorBlueGoogleMaps));

                                        mapFragment.getView().post(() -> { // run this on main thread
                                            mMap.addPolyline(polyline);
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));  // or use animateCamera() for smooth animation
                                            cafeteriaViewModel.setUpdating(false);
                                        });
                                    } else
                                        mapFragment.getView().post(() -> cafeteriaViewModel.setUpdating(false));
                                }));
                    }
                }
            });
        }
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

    public void addDish(View view) {
        Intent intent = new Intent(this, NewDishActivity.class);
        intent.putExtra("cafeteriaId", currentCafeteria.getId());
        startActivityForResult(intent, 1);
    }
}
