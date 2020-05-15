package pt.ulisboa.tecnico.cmov.foodist.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import pt.ulisboa.tecnico.cmov.foodist.BasicApp;
import pt.ulisboa.tecnico.cmov.foodist.cache.DualCache;

/**
 * BroadcastReceiver allowing reception of SUPPLICANT_CONNECTION_CHANGE_ACTION to allow
 * cache preloading on wifi connections.
 */
public class WifiReceiver extends BroadcastReceiver {
    private static final String TAG = WifiReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                Log.d(TAG, "connected");
                DualCache.preloadFirstPictures((BasicApp) context.getApplicationContext());
            } else {
                // wifi connection was lost
                Log.d(TAG, "disconnected");
            }
        }
    }

    /**
     * Checks if the device is connected via wifi, compatible with old and new SDKs to fix the
     * deprecation issue in Oreo:
     * https://developer.android.com/about/versions/oreo/background
     *
     * @param context The Context used to get system connectivity service
     * @return true if the device is connected via wifi
     */
    public static boolean isConnectedViaWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
            } else {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
            }
        }
        return false;
    }
}
