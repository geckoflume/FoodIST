package pt.ulisboa.tecnico.cmov.foodist;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import pt.ulisboa.tecnico.cmov.foodist.ui.UiUtils;

public abstract class PermissionsHelper {
    private static final String TAG = PermissionsHelper.class.getSimpleName();
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 01;

    /**
     * Return the current state of the permissions needed.
     */
    public static boolean checkPermissions(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private static void startLocationPermissionRequest(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    public static void requestPermissions(Activity activity) {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context");
            UiUtils.showSnackbar(activity.findViewById(android.R.id.content), R.string.permission_rationale, android.R.string.ok, Snackbar.LENGTH_INDEFINITE, view ->
                    startLocationPermissionRequest(activity));
        } else {
            Log.i(TAG, "Requesting permission");
            startLocationPermissionRequest(activity);
        }
    }
}
