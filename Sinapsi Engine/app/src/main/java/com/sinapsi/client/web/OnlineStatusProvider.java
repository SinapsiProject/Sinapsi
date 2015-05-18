package com.sinapsi.client.web;

/**
 * Interface with a callback to allow WebService facades to ask an external provider
 * if the current device has a working internet connection.
 */
public interface OnlineStatusProvider {
    public boolean isOnline();
}
