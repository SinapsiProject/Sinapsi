package com.sinapsi.android.activators;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;

import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.Event;
import com.sinapsi.model.Trigger;

import java.util.ArrayList;
import java.util.List;

public abstract class BroadcastActivator extends BroadcastReceiver {
    private ContextWrapper contextWrapper;
    private IntentFilter intentFilter;
    private DeviceInterface deviceInterface;
    private boolean registered = false;

    private List<Trigger> triggers = new ArrayList<>();
    public BroadcastActivator(IntentFilter iF, ContextWrapper cw, DeviceInterface di) {
        contextWrapper = cw;
        intentFilter = iF;
        deviceInterface = di;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        Event e = extractEventInfo(context, intent);
        for(Trigger t: triggers){
            t.activate(e,deviceInterface);
        }
    }

    public abstract Event extractEventInfo(Context c, Intent i);

    public void register(){
        contextWrapper.registerReceiver(this,intentFilter);
        registered = true;
    }

    public void unregister(){
        contextWrapper.unregisterReceiver(this);
        registered = false;
    }

    public void addTrigger(Trigger t){
        triggers.add(t);
    }

    public int getTriggersCount(){
        return triggers.size();
    }

    public void removeTrigger(Trigger t){
        triggers.remove(t); //TODO: check
    }

    public boolean isRegistered() {
        return registered;
    }
}
