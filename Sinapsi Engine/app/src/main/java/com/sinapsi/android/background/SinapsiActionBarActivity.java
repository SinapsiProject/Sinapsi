package com.sinapsi.android.background;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sinapsi.android.SinapsiAndroidApplication;
import com.sinapsi.android.utils.TempParameterManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity extension to identify ActionBarActivities binded with SinapsiBackgroundService
 */
public class SinapsiActionBarActivity extends AppCompatActivity implements ServiceConnectionListener {



    private ServiceConnectionBridge bridge = new ServiceConnectionBridge(this);

    /**
     * The background service object.
     */
    protected SinapsiBackgroundService service = null;

    private List<SinapsiFragment> fragments = new ArrayList<>();

    private TempParameterManager tempParameterManager;

    protected Object[] params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tempParameterManager = ((SinapsiAndroidApplication)getApplication()).getParameterManager();
        if(getIntent().hasExtra(TempParameterManager.EXTRA_PARAM_KEY))
            params = pullTempParameters();

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

    /**
     * This method can be overridden to do something after service connection
     * @param name
     */
    @Override
    public void onServiceConnected(ComponentName name) {
        service = bridge.get();
        for(SinapsiFragment f: fragments){
            f.onServiceConnected(service);
        }
    }

    /**
     * This method can be overridden to do something after service disconnection
     * @param name
     */
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



    public void returnActivity(Object... params){
        setResult(RESULT_OK, tempParameterManager.newIntentForTempParameters(params));
        finish();
    }

    public void cancelActivity(){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void startActivity(Class<?> target, Object... parameters){
        startActivity(generateParameterizedIntent(target, parameters));
    }

    public void startActivity(Class<?> target, ActivityReturnCallback callback, Object... parameters){
        startActivityForResult(
                generateParameterizedIntent(target, parameters),
                tempParameterManager.addReturnCallback(callback));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityReturnCallback callback = tempParameterManager.pullReturnCallback(requestCode);
        if(resultCode == RESULT_CANCELED) callback.onActivityCancel();
        else callback.onActivityReturn(tempParameterManager.pullTempParameters(data));

    }

    public interface ActivityReturnCallback{
        public void onActivityReturn(Object... returnValues);
        public void onActivityCancel();
    }

}
