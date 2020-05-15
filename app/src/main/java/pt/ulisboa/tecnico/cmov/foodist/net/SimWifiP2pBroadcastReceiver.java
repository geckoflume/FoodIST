package pt.ulisboa.tecnico.cmov.foodist.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;


/**
 * BroadcastReceiver allowing reception of WIFI_P2P_STATE_CHANGED_ACTION,
 * WIFI_P2P_PEERS_CHANGED_ACTION, WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION,
 * WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION to allow wifi direct beacon detection.
 */
public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = SimWifiP2pBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // This action is triggered when the Termite service changes state:
            // - creating the service generates the WIFI_P2P_STATE_ENABLED event
            // - destroying the service generates the WIFI_P2P_STATE_DISABLED event
            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);

            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
                Log.d(TAG, "WiFi Direct enabled");
            } else {
                Log.d(TAG, "WiFi Direct disabled");
            }
        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            Log.d(TAG, "Peer list changed");

        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {
            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            ginfo.print();
            Log.d(TAG, "Network membership changed");
        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {
            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            ginfo.print();
            Log.d(TAG, "Group ownership changed");
        }
    }
}
