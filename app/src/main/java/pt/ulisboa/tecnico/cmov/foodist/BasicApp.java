package pt.ulisboa.tecnico.cmov.foodist;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Messenger;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.concurrent.Executor;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.ulisboa.tecnico.cmov.foodist.cache.DualCache;
import pt.ulisboa.tecnico.cmov.foodist.db.AppDatabase;
import pt.ulisboa.tecnico.cmov.foodist.db.DataRepository;
import pt.ulisboa.tecnico.cmov.foodist.net.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.foodist.net.WifiReceiver;

/**
 * Android Application class.
 * Used for accessing singletons, initializing libs and BroadcastReceivers.
 */
public class BasicApp extends Application implements PeerListListener {
    private AppExecutors mAppExecutors;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private boolean mBound = false;
    private SimWifiP2pBroadcastReceiver mReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
        AndroidThreeTen.init(this);
        IntentFilter intentFilterSupplicant = new IntentFilter();
        intentFilterSupplicant.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        registerReceiver(new WifiReceiver(), intentFilterSupplicant);
        if (WifiReceiver.isConnectedViaWifi(this))
            DualCache.preloadFirstPictures(this);

        IntentFilter intentFilterTermite = new IntentFilter();
        intentFilterTermite.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilterTermite.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilterTermite.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        intentFilterTermite.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver();
        registerReceiver(mReceiver, intentFilterTermite);

        Intent intent = new Intent(this, SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getApplicationContext(), getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }

    public Executor networkIO() {
        return mAppExecutors.networkIO();
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {
        // TODO: find the corresponding cafeteria id based on the cafeteria and call the
        // insertBeacon() and updateBeacon() methods
    }
}
