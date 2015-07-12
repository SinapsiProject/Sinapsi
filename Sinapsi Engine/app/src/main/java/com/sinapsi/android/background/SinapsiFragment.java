package com.sinapsi.android.background;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;

import com.sinapsi.android.Lol;

/**
 * Fragment extension representing a Fragment that refers to a SinapsiBackgroundService
 */
public abstract class SinapsiFragment extends Fragment {

    public abstract String getName(Context context);

    protected SinapsiBackgroundService service = null;
    private boolean connectedToService = false;
    private boolean isVisible = false;


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

    public Intent generateParameterizedIntent(Class<?> target, Object... params){
        Activity a = getActivity();
        if(a instanceof SinapsiActionBarActivity){
            return ((SinapsiActionBarActivity) a).generateParameterizedIntent(target, params);
        }else {
            throw new RuntimeException("Sinapsi Fragment can be child only of a SinapsiActivity or SinapsiActionBarActivity");
        }
    }

    public void startActivity(Class<?> target, Object... parameters){
        Activity a = getActivity();
        if(a instanceof SinapsiActionBarActivity){
            ((SinapsiActionBarActivity) a).startActivity(target, parameters);
        }else {
            throw new RuntimeException("Sinapsi Fragment can be child only of a SinapsiActivity or SinapsiActionBarActivity");
        }
    }

    public void startActivity(SinapsiActionBarActivity.ActivityReturnCallback callback, Class<?> target, Object... parameters){
        Activity a = getActivity();
        if(a instanceof SinapsiActionBarActivity){
            ((SinapsiActionBarActivity) a).startActivity(target, callback, parameters);
        }else {
            throw new RuntimeException("Sinapsi Fragment can be child only of a SinapsiActivity or SinapsiActionBarActivity");
        }
    }

    public void returnActivity(Object... params){
        Activity a = getActivity();
        if(a instanceof SinapsiActionBarActivity){
            ((SinapsiActionBarActivity) a).returnActivity(params);
        }else {
            throw new RuntimeException("Sinapsi Fragment can be child only of a SinapsiActivity or SinapsiActionBarActivity");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof SinapsiActionBarActivity){
            // todo: get temp parameters
        }else{
            throw new RuntimeException("Sinapsi Fragment can be child only of a SinapsiActivity or SinapsiActionBarActivity");
        }
    }
}
