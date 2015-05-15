package com.sinapsi.android.background;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Utility class used to handle a SinapsiBackgroundService connection
 */
public class ServiceConnectionBridge implements ServiceConnection{

    private SinapsiBackgroundService backgroundService;

    private ServiceConnectionListener listener;

    public ServiceConnectionBridge(ServiceConnectionListener listener){
        this.listener = listener;
    }

    public ServiceConnectionBridge(){
        this.listener = null;
    }

    public void bind(Context context){
        context.bindService(new Intent(context, SinapsiBackgroundService.class), this, Context.BIND_AUTO_CREATE);
    }

    public void unbind(Context context){
        context.unbindService(this);
        backgroundService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        backgroundService = ((SinapsiBackgroundService.SinapsiServiceBinder) service).getService();
        if(listener!=null) listener.onServiceConnected(name);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        backgroundService = null;
        if(listener!=null) listener.onServiceDisconnected(name);
    }

    public SinapsiBackgroundService get(){
        return backgroundService;
    }

    public boolean isConnected(){
        return backgroundService != null;
    }

}
