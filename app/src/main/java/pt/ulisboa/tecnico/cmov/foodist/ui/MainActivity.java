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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
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

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.BuildConfig;
import pt.ulisboa.tecnico.cmov.foodist.PermissionsHelper;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.model.Campus;
import pt.ulisboa.tecnico.cmov.foodist.model.Status;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaListViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private AppBarConfiguration mAppBarConfiguration;
    private ArrayAdapter<Campus> adapterCampus;
    private Spinner spinner;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private CafeteriaListViewModel mCafeteriaListViewModel;
    private SharedPreferences sharedPref;
    private MutableLiveData<Boolean> canRefresh = new MutableLiveData<>(false);
    private MenuItem refreshMenuItem;
    private TextView textView_username;
    private TextView textView_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer and NavigationUI
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_cafeterias, R.id.nav_account).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Account data
        textView_username = navigationView.getHeaderView(0).findViewById(R.id.textView_username);
        textView_email = navigationView.getHeaderView(0).findViewById(R.id.textView_email);
        updateUser();

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterCampus = new ArrayAdapter<>(this, R.layout.layout_drop_campus, Campus.getInstance(this));
        // Specify the layout to use when the list of choices appears
        adapterCampus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner = navigationView.getHeaderView(0).findViewById(R.id.spinner);
        spinner.setAdapter(adapterCampus);
        //spinner.setSelection(sharedPref.getInt(getString(R.string.campus_id_key), 1));
        spinner.setOnItemSelectedListener(campusSelectedCallback());
        spinner.setSelection(Campus.DEFAULT);

        // Cafeteria ListView
        mCafeteriaListViewModel = new ViewModelProvider(this).get(CafeteriaListViewModel.class);
        mCafeteriaListViewModel.setStatus(sharedPref.getInt("status", Status.DEFAULT));

        // Refresh button
        canRefresh.observe(this, canRefresh -> {
            if (canRefresh) {
                refreshMenuItem.setEnabled(true);
                spinner.setSelection(Campus.AUTODETECT);
                updateCafeterias();
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

        if (PermissionsHelper.checkPermissionLocation(this))
            startLocationUpdates();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!PermissionsHelper.checkPermissionLocation(this))
            PermissionsHelper.requestPermissionLocation(MainActivity.this);
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
            case R.id.action_test:
                Log.d(TAG, "Test option selected");
                break;
        }
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void updateCafeterias() {
            if (mCurrentLocation != null)
                ((BasicApp) MainActivity.this.getApplication()).networkIO().execute(() -> {
                    mCafeteriaListViewModel.setUpdating(true);
                    mCafeteriaListViewModel.updateCafeteriasDistances(mCurrentLocation, getString(R.string.google_maps_key));
                    mCafeteriaListViewModel.updateCafeteriasWaitTimes();
                    mCafeteriaListViewModel.setUpdating(false);
                });
            else {
                mCafeteriaListViewModel.setUpdating(true);
                Toast.makeText(MainActivity.this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
                mCafeteriaListViewModel.updateCafeteriasWaitTimes();
                mCafeteriaListViewModel.setUpdating(false);
            }
    }

    private AdapterView.OnItemSelectedListener campusSelectedCallback() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == Campus.AUTODETECT) {
                    // "Find nearest campus" selected
                    if (mCurrentLocation != null && PermissionsHelper.checkPermissionLocation(MainActivity.this)) {
                        spinner.setEnabled(false);
                        Toast.makeText(MainActivity.this, R.string.autodetecting_campus_toast, Toast.LENGTH_LONG).show();
                        Campus nearest = Campus.findNearest(getApplicationContext(), mCurrentLocation);
                        spinner.setSelection(adapterCampus.getPosition(nearest));
                        spinner.setEnabled(true);
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.need_location), Toast.LENGTH_LONG).show();
                        PermissionsHelper.requestPermissionLocation(MainActivity.this);
                    }
                } else {
                    // apply() to commit asynchronously
                    sharedPref.edit().putInt(getString(R.string.campus_id_key), position).apply();
                    mCafeteriaListViewModel.setCampus(position);
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                // Permission granted.
                startLocationUpdates();
            } else {
                UiUtils.showSnackbar(findViewById(android.R.id.content), R.string.permission_denied_explanation_location, R.string.action_settings, Snackbar.LENGTH_LONG, view -> {
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
                if (!canRefresh.getValue()) {
                    Log.d(TAG, "Toggling refresh button from createLocationCallback");
                    canRefresh.setValue(true);
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
                                Log.e(TAG, "PendingIntent unable to execute request.");
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

    public void updateUser() {
        textView_username.setText(sharedPref.getString("username", getString(R.string.default_username)) + " - " + Status.getInstance(this).get(sharedPref.getInt("status", Status.DEFAULT)));
        textView_email.setText(sharedPref.getString("email", getString(R.string.default_email)));
    }

    public void updateStatus() {
        mCafeteriaListViewModel.setStatus(sharedPref.getInt("status", Status.DEFAULT));
    }
}
