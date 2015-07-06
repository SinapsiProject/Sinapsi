package com.sinapsi.android;

import android.app.Application;

import com.sinapsi.android.utils.TempParameterManager;

/**
 * Application extension for shared object management across all the
 * app's entry points.
 */
public class SinapsiAndroidApplication extends Application {

    private boolean isLoggedIn = false;

    private TempParameterManager parameterManager = new TempParameterManager();

    public TempParameterManager getParameterManager(){
        return parameterManager;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }
}
