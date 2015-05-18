package com.sinapsi.android.background;

import android.content.ComponentName;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity extension to identify ActionBarActivities binded with SinapsiBackgroundService
 */
public class ServiceBoundActionBarActivity extends ActionBarActivity implements ServiceConnectionListener {

    private ServiceConnectionBridge bridge = new ServiceConnectionBridge(this);

    /**
     * The background service object.
     */
    protected SinapsiBackgroundService service = null;

    private List<ServiceBoundFragment> fragments = new ArrayList<>();


    @Override
    protected void onStart() {
        super.onResume();
        bridge.bind(this);
    }

    @Override
    protected void onStop() {
        super.onPause();
        bridge.unbind(this);
    }

    /**
     * This method can be overridden to do something after service connection
     * @param name
     */
    @Override
    public void onServiceConnected(ComponentName name) {
        service = bridge.get();
        for(ServiceBoundFragment f: fragments){
            f.onServiceConnected(service);
        }
    }

    /**
     * This method can be overridden to do something after service connection
     * @param name
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
        for(ServiceBoundFragment f: fragments){
            f.onServiceDisconnected();
        }
    }

    public boolean isServiceConnected(){
        return bridge.isConnected();
    }

    public void addFragmentForConnectionListening(ServiceBoundFragment f){
        fragments.add(f);
    }
}
