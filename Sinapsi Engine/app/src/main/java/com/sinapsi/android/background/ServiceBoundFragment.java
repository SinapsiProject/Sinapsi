package com.sinapsi.android.background;

import android.app.Fragment;

/**
 * Fragment extension representing a Fragment that refers to a SinapsiBackgroundService
 */
public class ServiceBoundFragment extends Fragment {

    protected SinapsiBackgroundService service = null;
    private boolean connectedToService = false;


    public void onServiceConnected(SinapsiBackgroundService service){
        this.service = service;
        connectedToService = true;
    }

    public void onServiceDisconnected(){
        this.service = null;
        connectedToService = false;
    }

    public boolean isServiceConnected() {
        return connectedToService;
    }
}
