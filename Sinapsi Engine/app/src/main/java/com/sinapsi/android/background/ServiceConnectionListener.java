package com.sinapsi.android.background;

import android.content.ComponentName;

/**
 * Interface to be implemented in order to handle background
 * service connection related events.
 */
public interface ServiceConnectionListener {
    public void onServiceConnected(ComponentName name);

    public void onServiceDisconnected(ComponentName name);

}
