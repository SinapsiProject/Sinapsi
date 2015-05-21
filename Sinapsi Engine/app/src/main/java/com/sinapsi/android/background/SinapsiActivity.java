package com.sinapsi.android.background;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.sinapsi.android.SinapsiAndroidApplication;
import com.sinapsi.android.TempParameterManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity extension to identify Activities binded with SinapsiBackgroundService
 */
public class SinapsiActivity extends Activity implements ServiceConnectionListener{

    private ServiceConnectionBridge bridge = new ServiceConnectionBridge(this);

    /**
     * The background service object.
     */
    protected SinapsiBackgroundService service = null;

    private List<SinapsiFragment> fragments = new ArrayList<>();

    private TempParameterManager tempParameterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tempParameterManager = ((SinapsiAndroidApplication)getApplication()).getParameterManager();
    }

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
        for(SinapsiFragment f: fragments){
            f.onServiceConnected(service);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
        for(SinapsiFragment f: fragments){
            f.onServiceDisconnected();
        }
    }

    public boolean isServiceConnected(){
        return bridge.isConnected();
    }

    public void addFragmentForConnectionListening(SinapsiFragment f){
        fragments.add(f);
    }

    public Intent generateParameterizedIntent(Class<?> target, Object... parameters){
        return tempParameterManager.newIntentForTempParameters(this, target, parameters);
    }

    public TempParameterManager getTempParameterManager() {
        return tempParameterManager;
    }

    public Object[] getTempParameters(){
        return tempParameterManager.getTempParameters(getIntent());
    }

    public Object[] pullTempParameters(){
        return tempParameterManager.pullTempParameters(getIntent());
    }

 }
