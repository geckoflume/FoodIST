package pt.ulisboa.tecnico.cmov.foodist.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.foodist.BuildConfig;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.location.LocationUtils;
import pt.ulisboa.tecnico.cmov.foodist.model.Campus;
import pt.ulisboa.tecnico.cmov.foodist.viewmodel.CafeteriaListViewModel;

import static pt.ulisboa.tecnico.cmov.foodist.ui.UiUtils.showSnackbar;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 01;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private List<Campus> campuses = new ArrayList<>();
    private ArrayAdapter<Campus> adapterCampus;
    private Spinner spinner;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        initToolbar(toolbar);

        RecyclerView recyclerViewCafeterias = findViewById(R.id.recyclerView_cafeterias);
        CafeteriaAdapter adapterCafeterias = new CafeteriaAdapter(this);
        recyclerViewCafeterias.setAdapter(adapterCafeterias);
        // Get a new or existing ViewModel from the ViewModelProvider.
        CafeteriaListViewModel mCafeteriaListViewModel = new ViewModelProvider(this).get(CafeteriaListViewModel.class);
        // Add an observer on the LiveData returned by getCafeterias.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the cafeterias in the adapter.
        mCafeteriaListViewModel.getCafeterias().observe(this, adapterCafeterias::setCafeterias);


        initCampuses();
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterCampus = new ArrayAdapter<>(this, R.layout.layout_drop_item, campuses);
        // Specify the layout to use when the list of choices appears
        adapterCampus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner = new Spinner(toolbar.getContext());
        spinner.setAdapter(adapterCampus);
        toolbar.addView(spinner);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {                                        // "Find nearest campus"
                    Toast.makeText(MainActivity.this, R.string.autodetecting_campus_toast, Toast.LENGTH_LONG).show();
                    startLocationUpdates();
                } else if (position == 1) {                                 // "All cafeterias"
                    mCafeteriaListViewModel.setQuery("");
                } else if (position > 1 && position < campuses.size()) {    // Campus selected
                    mCafeteriaListViewModel.setQuery(String.valueOf(position - 1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkPermissions();
    }

    private void initCampuses() {
        Campus campus = new Campus(0, "Find nearest campus", 0, 0);
        this.campuses.add(campus);
        campus = new Campus(1, "All cafeterias", 0, 0);
        this.campuses.add(campus);
        campus = new Campus(2, "Alameda", 38.736795, -9.138637);
        this.campuses.add(campus);
        campus = new Campus(3, "Taguspark", 38.737461, -9.303161);
        this.campuses.add(campus);
        campus = new Campus(4, "Tecnológico e Nuclear", 38.811911, -9.094221);
        this.campuses.add(campus);
    }

    /**
     * Return the current state of the permissions needed.
     */
    private void checkPermissions() {
        if (!(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissions();
        }
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("main", "Displaying permission rationale to provide additional context");
            showSnackbar(findViewById(android.R.id.content), R.string.permission_rationale,
                    android.R.string.ok, Snackbar.LENGTH_INDEFINITE, view -> {
                        startLocationPermissionRequest();
                    });
        } else {
            Log.i("main", "Requesting permission");
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i("main", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted.
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow. (TODO)

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(findViewById(android.R.id.content),
                        R.string.permission_denied_explanation,
                        R.string.settings,
                        Snackbar.LENGTH_LONG,
                        view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID,
                                    null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
            }
        }
    }

    /**
     * Find and update nearest campus spinner
     */
    private void updateNearestCampus() {
        Campus nearest = null;
        float distanceNearest = 0.f;
        float distanceCandidate;
        for (Campus c : campuses) {
            if (nearest == null) {
                nearest = c;
                distanceNearest =
                        LocationUtils.calculateDistance(mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude(),
                                nearest.getLatitude(),
                                nearest.getLongitude());
            } else {
                distanceCandidate =
                        LocationUtils.calculateDistance(mCurrentLocation.getLatitude(),
                                mCurrentLocation.getLongitude(),
                                c.getLatitude(),
                                c.getLongitude());
                if (distanceCandidate < distanceNearest) {
                    nearest = c;
                    distanceNearest =
                            LocationUtils.calculateDistance(mCurrentLocation.getLatitude(),
                                    mCurrentLocation.getLongitude(),
                                    nearest.getLatitude(),
                                    nearest.getLongitude());
                }
            }
        }
        Log.i("Campus", "Nearest campus is " + nearest + " at " + distanceNearest + "m.");
        this.spinner.setSelection(this.adapterCampus.getPosition(nearest));
    }

    /**
     * Sets up the location request.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
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
                stopLocationUpdates();
                updateNearestCampus();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("main", "User agreed to make required location settings changes.");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("main", "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     * Note: we don't call this unless location runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the user has granted location permissions
        checkPermissions();

        // Then by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Log.i("main", "All location settings are satisfied.");

                    //mLocationRequest.setNumUpdates(1); // Not useful, we only want to request on location update but stop is triggered on onLocationResult
                    mLocationRequest.setExpirationDuration(1000 * 5); // Expire in 5s

                    //noinspection MissingPermission
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, Looper.myLooper());
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i("main", "Location settings are not satisfied. Attempting to upgrade location settings ");
                            try {
                                // Show the dialog by calling startResolutionForResult(), and
                                // check the result in onActivityResult().
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(MainActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.i("main", "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                            Log.e("main", errorMessage);
                            Toast.makeText(MainActivity.this, errorMessage,
                                    Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void initToolbar(MaterialToolbar toolbar) {
        toolbar.setNavigationOnClickListener(v -> Toast.makeText(MainActivity.this, toolbar.getTitle(), Toast.LENGTH_LONG).show());
        toolbar.setOnMenuItemClickListener(menuItem -> {
            Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
            return true;
        });
    }
}