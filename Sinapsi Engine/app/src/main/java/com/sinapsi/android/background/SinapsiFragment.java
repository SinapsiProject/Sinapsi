package com.sinapsi.android.background;

import android.app.Fragment;
import android.content.Context;

/**
 * Fragment extension representing a Fragment that refers to a SinapsiBackgroundService
 */
public abstract class SinapsiFragment extends Fragment {

    public abstract String getName(Context context);

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
