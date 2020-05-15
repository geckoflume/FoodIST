package pt.ulisboa.tecnico.cmov.foodist;

import android.app.Application;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.concurrent.Executor;

import pt.ulisboa.tecnico.cmov.foodist.cache.DualCache;
import pt.ulisboa.tecnico.cmov.foodist.db.AppDatabase;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.net.WifiReceiver;

/**
 * Android Application class.
 * Used for accessing singletons, initializing libs and BroadcastReceivers.
 */
public class BasicApp extends Application {
    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
        AndroidThreeTen.init(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        registerReceiver(new WifiReceiver(), intentFilter);
        if (WifiReceiver.isConnectedViaWifi(this))
            DualCache.preloadFirstPictures(this);
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }

    public Executor networkIO() {
        return mAppExecutors.networkIO();
    }
}
