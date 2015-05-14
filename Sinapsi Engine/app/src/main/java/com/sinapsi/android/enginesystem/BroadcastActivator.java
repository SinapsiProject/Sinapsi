package com.sinapsi.android.enginesystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;

import com.sinapsi.engine.Event;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.Trigger;

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
    private ExecutionInterface executionInterface;
    private AndroidActivationManager activationManager;
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
    public BroadcastActivator(AndroidActivationManager aM, IntentFilter iF, ContextWrapper cw, ExecutionInterface di) {
        this.activationManager = aM;
        contextWrapper = cw;
        intentFilter = iF;
        executionInterface = di;
        di.getLog().log("ANDROIDACTMAN", "New BroadcastActivator");
    }

    @Override
    public void onReceive(Context context, Intent intent){
        if(!activationManager.isEnabled()) return;
        executionInterface.getLog().log("ANDROIDACTMAN", "Received intent action: " + intent.getAction());
        Event e = extractEventInfo(context, intent);
        for(Trigger t: triggers){
            ExecutionInterface ei = executionInterface.cloneInstance();
            t.activate(e, ei);
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
