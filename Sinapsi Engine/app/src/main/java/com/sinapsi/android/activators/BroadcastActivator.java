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

/**
 * Extending class for managing the activation of triggers linked
 * to a specific intent filter. This must be extended in order to
 * specify how Event is created from the Intent and the Context at
 * execution phase.
 */
public abstract class BroadcastActivator extends BroadcastReceiver {
    private ContextWrapper contextWrapper;
    private IntentFilter intentFilter;
    private DeviceInterface deviceInterface;
    private boolean registered = false;

    private List<Trigger> triggers = new ArrayList<>();

    /**
     * BroadcastActivator ctor.
     * @param iF The intent filter of the broadcast receiver
     * @param cw The context wrapper on which the broadcast
     *           receiver is registered and unregistered
     * @param di the device interface, passed to triggers
     *           when activated.
     */
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

    /**
     * Method to be implemented in order to create an
     * Event instance containing all useful infos
     * from Intent and Context
     * @param c the context
     * @param i the intent of the event
     * @return a new Event instance
     */
    public abstract Event extractEventInfo(Context c, Intent i);

    /**
     * Registers this BroadcastReceiver on the contextWrapper
     * with the IntentFilter.
     */
    public void register(){
        contextWrapper.registerReceiver(this,intentFilter);
        registered = true;
    }

    /**
     * Unregisters this BroadcastReceiver from the contextWrapper
     */
    public void unregister(){
        contextWrapper.unregisterReceiver(this);
        registered = false;
    }


    /**
     * Adds the specified Trigger to a list, in order to be notified
     * when this BroadcastReceiver receives a new event from system
     * @param t the trigger
     */
    public void addTrigger(Trigger t){
        triggers.add(t);
    }

    public int getTriggersCount(){
        return triggers.size();
    }

    /**
     * Removes the trigger from the "notification" list
     * @param t the trigger
     */
    public void removeTrigger(Trigger t){
        triggers.remove(t); //TODO: check
    }

    /**
     * This BroadcastReceiver's registration getter
     * @return true if this BroadcastReceiver is registered
     *         on a ContextWrapper, false otherwise.
     */
    public boolean isRegistered() {
        return registered;
    }
}
