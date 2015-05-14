package com.sinapsi.android.background;

import android.app.Activity;
import android.content.ComponentName;

/**
 * Activity extension to identify Activities binded with SinapsiBackgroundService
 */
public class ServiceBindedActivity extends Activity implements ServiceConnectionListener{

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

    @Override
    public void onServiceConnected(ComponentName name) {
        service = bridge.get();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

}
