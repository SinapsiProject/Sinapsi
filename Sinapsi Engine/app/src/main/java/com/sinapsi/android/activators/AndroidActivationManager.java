package com.sinapsi.android.activators;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.sinapsi.engine.ActivationManager;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.Event;
import com.sinapsi.model.Trigger;
import com.sinapsi.model.components.TriggerWifi;

import java.util.Arrays;

/**
 * ActivationManager - implementation for the Android platform.
 * This handles the trigger activation using android-exclusive
 * mechanisms like BroadcastReceivers.
 */
public class AndroidActivationManager implements ActivationManager {

    private ContextWrapper contextWrapper;
    private DeviceInterface deviceInterface;

    //TODO: move this initialization in a ctor, which checks
    //----: if wifi is available on the deviceInterface
    private BroadcastActivator wifiActivator = new BroadcastActivator(
            newIntentFilter(new String[]{
                    "android.net.wifi.STATE_CHANGE",
                    "android.net.wifi.WIFI_STATE_CHANGED"
            }), contextWrapper, deviceInterface) {
        @Override
        public Event extractEventInfo(Context c, Intent i) {
            return null;
        }
    };

    private BroadcastActivator[] activators = new BroadcastActivator[]{
            wifiActivator
    };

    /**
     * Creates a new AndroidActivationManager instance with the specified
     * ContextWrapper and DeviceInterface.
     * @param cw the contextWrapper
     * @param di the deviceInterface
     */
    public AndroidActivationManager(ContextWrapper cw, DeviceInterface di){
        this.contextWrapper = cw;
        this.deviceInterface = di;
    }

    @Override
    public void addToNotifyList(Trigger t) {
        if(t.getName().equals(TriggerWifi.TRIGGER_WIFI)) wifiActivator.addTrigger(t);

        manageRegistrations();
    }

    @Override
    public void removeFromNotifyList(Trigger t) {
        if(t.getName().equals(TriggerWifi.TRIGGER_WIFI)) wifiActivator.removeTrigger(t);

        manageRegistrations();
    }

    /**
     * Used to register the broadcast receivers on the system only
     * when needed.
     */
    private void manageRegistrations(){
        for(BroadcastActivator ba: activators){
            if(ba.getTriggersCount() == 0 && ba.isRegistered())
                ba.unregister();
            else if (ba.getTriggersCount()>0 && !ba.isRegistered()){
                ba.register();
            }
        }
    }

    /**
     * Helper method to create a new intent filter.
     * @param actions an array containing the actions of
     *                this intent filter
     * @return a new IntentFilter instance.
     */
    public static IntentFilter newIntentFilter(String[] actions){
        IntentFilter iF = new IntentFilter();
        for(String s: actions){
            iF.addAction(s);
        }
        return iF;
    }
}
