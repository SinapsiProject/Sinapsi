package com.sinapsi.android.background;

/**
 * Listener interface with method called when the background
 * service detects a change in the connection against the web
 * service.
 */
public interface WebServiceConnectionListener {
    public void onOnlineMode();
    public void onOfflineMode();
}
