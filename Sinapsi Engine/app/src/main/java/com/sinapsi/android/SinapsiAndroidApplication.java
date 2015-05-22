package com.sinapsi.android;

import android.app.Application;

/**
 * Application extension for shared object management across all the
 * app's entry points.
 */
public class SinapsiAndroidApplication extends Application {

    private TempParameterManager parameterManager = new TempParameterManager();

    public TempParameterManager getParameterManager(){
        return parameterManager;
    }

}
