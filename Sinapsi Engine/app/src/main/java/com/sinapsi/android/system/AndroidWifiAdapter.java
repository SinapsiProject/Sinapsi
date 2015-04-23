package com.sinapsi.android.system;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.sinapsi.engine.system.WifiAdapter;
import com.sinapsi.model.parameters.ConnectionStatusChoices;
import com.sinapsi.model.parameters.SwitchStatusChoices;

/**
 * WifiAdapter implementation for Android.
 */
public class AndroidWifiAdapter implements WifiAdapter{

    //TODO: add proper permissions to manifest

    private WifiManager wm;
    private ConnectivityManager cm;

    public AndroidWifiAdapter(Context context){
        wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public SwitchStatusChoices getStatus() {
        int s = wm.getWifiState();
        switch (s){
            case WifiManager.WIFI_STATE_DISABLED:
                return SwitchStatusChoices.DISABLED;
            case WifiManager.WIFI_STATE_DISABLING:
                return SwitchStatusChoices.DISABLING;
            case WifiManager.WIFI_STATE_ENABLED:
                return SwitchStatusChoices.ENABLED;
            case WifiManager.WIFI_STATE_ENABLING:
                return SwitchStatusChoices.ENABLING;
            case WifiManager.WIFI_STATE_UNKNOWN:
            default:
                return null;
        }
    }

    @Override
    public ConnectionStatusChoices getConnectionStatus() {
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(ni == null) return ConnectionStatusChoices.DISCONNECTED;
        if(ni.isConnected()&&!ni.isConnectedOrConnecting()){
            return ConnectionStatusChoices.CONNECTED;
        }else if(ni.isConnectedOrConnecting()){
            return ConnectionStatusChoices.CONNECTING;
        }else{
            return ConnectionStatusChoices.DISCONNECTED;
        }
    }

    @Override
    public String getSSID() {
        return wm.getConnectionInfo().getSSID();
    }

    @Override
    public void connectToSSID(String id) {
        //TODO: probably this is not available in Android. check
    }

    @Override
    public void setStatus(boolean status) {
        wm.setWifiEnabled(status);
    }

    @Override
    public void setConnectionStatus(boolean status) {
        if(status){
            wm.reconnect();
        }else{
            wm.disconnect();
        }
    }
}