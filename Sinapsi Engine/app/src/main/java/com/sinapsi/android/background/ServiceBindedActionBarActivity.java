package com.sinapsi.android.background;

import android.content.ComponentName;
import android.support.v7.app.ActionBarActivity;

/**
 * Activity extension to identify ActionBarActivities binded with SinapsiBackgroundService
 */
public class ServiceBindedActionBarActivity extends ActionBarActivity implements ServiceConnectionListener {

    private ServiceConnectionBridge bridge = new ServiceConnectionBridge(this);

    /**
     * The background service object.
     */
    protected SinapsiBackgroundService service = null;


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
    }

    /**
     * This method can be overridden to do something after service connection
     * @param name
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    public boolean isServiceConnected(){
        return bridge.isConnected();
    }


}
