package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.BuildConfig;
import pt.ulisboa.tecnico.cmov.foodist.PermissionsHelper;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.db.entity.CafeteriaEntity;
import pt.ulisboa.tecnico.cmov.foodist.model.Campus;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaListViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private AppBarConfiguration mAppBarConfiguration;
    private List<Campus> campuses = new ArrayList<>();
    private ArrayAdapter<Campus> adapterCampus;
    private List<CafeteriaEntity> currentCafeterias;
    private Spinner spinner;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private CafeteriaListViewModel mCafeteriaListViewModel;
    private SharedPreferences sharedPref;
    private boolean canRefresh = false;
    private MenuItem refreshMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getPreferences(Context.MODE_PRIVATE);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer and NavigationUI
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_cafeterias).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Campuses spinner
        initCampuses();
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterCampus = new ArrayAdapter<>(this, R.layout.layout_drop_item, campuses);
        // Specify the layout to use when the list of choices appears
        adapterCampus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner = navigationView.getHeaderView(0).findViewById(R.id.spinner);
        spinner.setAdapter(adapterCampus);
        spinner.setSelection(sharedPref.getInt(getString(R.string.campus_id_key), 1));
        spinner.setOnItemSelectedListener(campusSelectedCallback());

        // Cafeteria ListView
        mCafeteriaListViewModel = new ViewModelProvider(this).get(CafeteriaListViewModel.class);
        mCafeteriaListViewModel.getCafeterias().observe(this, cafeteriaEntities -> {
            currentCafeterias = cafeteriaEntities;
            if (!canRefresh && mCurrentLocation != null) {
                Log.i(TAG, "Toggling refresh button from observer");
                enableRefresh();
            }
        });

        // Location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        if (PermissionsHelper.checkPermissions(this))
            startLocationUpdates();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!PermissionsHelper.checkPermissions(this))
            PermissionsHelper.requestPermissions(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        refreshMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateCafeterias();
                break;
        }
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void updateCafeterias() {
        ((BasicApp) MainActivity.this.getApplication()).networkIO().execute(() ->
                mCafeteriaListViewModel.updateCafeteriasDistances(currentCafeterias, mCurrentLocation, getString(R.string.google_maps_key)));
    }

    private void initCampuses() {
        Campus campus = new Campus(0, getString(R.string.find_nearest), 0, 0);
        this.campuses.add(campus);
        campus = new Campus(1, getString(R.string.all_cafeterias), 0, 0);
        this.campuses.add(campus);
        campus = new Campus(2, getString(R.string.alameda), 38.736795, -9.138637);
        this.campuses.add(campus);
        campus = new Campus(3, getString(R.string.taguspark), 38.737461, -9.303161);
        this.campuses.add(campus);
        campus = new Campus(4, getString(R.string.ctn), 38.811911, -9.094221);
        this.campuses.add(campus);
    }

    private AdapterView.OnItemSelectedListener campusSelectedCallback() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (((Campus) parent.getItemAtPosition(position)).getName().equals(getString(R.string.find_nearest))) { // in case the campuses are inserted in a strange order
                    // "Find nearest campus" selected
                    if (mCurrentLocation != null && PermissionsHelper.checkPermissions(MainActivity.this)) {
                        spinner.setEnabled(false);
                        Toast.makeText(MainActivity.this, R.string.autodetecting_campus_toast, Toast.LENGTH_LONG).show();
                        Campus nearest = Campus.findNearest(campuses, mCurrentLocation);
                        spinner.setSelection(adapterCampus.getPosition(nearest));
                        spinner.setEnabled(true);
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.need_location), Toast.LENGTH_LONG).show();
                        PermissionsHelper.requestPermissions(MainActivity.this);
                    }
                } else {
                    // "All cafeterias" or campus selected
                    // apply() to commit asynchronously
                    sharedPref.edit().putInt(getString(R.string.campus_id_key), position).apply();
                    if (((Campus) parent.getItemAtPosition(position)).getName().equals(getString(R.string.all_cafeterias))) {
                        // "All cafeterias" selected
                        mCafeteriaListViewModel.setQuery("");
                    } else {
                        // Campus selected
                        mCafeteriaListViewModel.setQuery(String.valueOf(position - 1));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionsHelper.REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted.
                startLocationUpdates();
            } else {
                UiUtils.showSnackbar(findViewById(android.R.id.content), R.string.permission_denied_explanation, R.string.action_settings, Snackbar.LENGTH_LONG, view -> {
                    // Build intent that displays the App settings screen.
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
            }
        }
    }

    /**
     * Sets up the location request.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mCurrentLocation = locationResult.getLastLocation();

                // We fetch the location once, it is not useful to keep tracking the user
                stopLocationUpdates();
                if (!canRefresh && currentCafeterias != null) {
                    Log.i(TAG, "Toggling refresh button from createLocationCallback");
                    enableRefresh();
                }
            }
        };
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     * Note: we don't call this unless location runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Then by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Log.i(TAG, "All location settings are satisfied.");
                    mLocationRequest.setExpirationDuration(1000 * 30); // Expire in 30s
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                            Log.e(TAG, errorMessage);
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void enableRefresh() {
        canRefresh = true;
        refreshMenuItem.setEnabled(true);
    }
}
