package com.sinapsi.android.system;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsMessage;

import com.sinapsi.android.utils.IntentUtils;
import com.sinapsi.engine.ActivationManager;
import com.sinapsi.engine.Event;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.Trigger;
import com.sinapsi.model.components.TriggerSMS;
import com.sinapsi.model.components.TriggerWifi;

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

    //TODO: add proper permissions on the android manifest
    private BroadcastActivator smsActivator = new BroadcastActivator(
            newIntentFilter(new String[]{
                    "android.provider.Telephony.SMS_RECEIVED"
            }), contextWrapper, deviceInterface) {
        @Override
        public Event extractEventInfo(Context c, Intent intent) {
            try {
                SmsMessage[] messages = IntentUtils.getMessagesFromIntent(intent);
                //TODO: google why an sms intent may contain more messages
                //----: and check if just the first has to be read
                String phoneNumber = messages[0].getDisplayOriginatingAddress();
                String message = messages[0].getDisplayMessageBody();
                return new Event()
                        .put("sender_number", phoneNumber)
                        .put("message_content", message);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return null;
            }

        }
    };

    private BroadcastActivator[] activators = new BroadcastActivator[]{
            wifiActivator,
            smsActivator
    };

    /**
     * Creates a new AndroidActivationManager instance with the specified
     * ContextWrapper and DeviceInterface.
     *
     * @param cw the contextWrapper
     * @param di the deviceInterface
     */
    public AndroidActivationManager(ContextWrapper cw, DeviceInterface di) {
        this.contextWrapper = cw;
        this.deviceInterface = di;
    }

    @Override
    public void addToNotifyList(Trigger t) {
        if (t.getName().equals(TriggerWifi.TRIGGER_WIFI)) wifiActivator.addTrigger(t);
        if (t.getName().equals(TriggerSMS.TRIGGER_SMS)) smsActivator.addTrigger(t);
        manageRegistrations();
    }

    @Override
    public void removeFromNotifyList(Trigger t) {
        if (t.getName().equals(TriggerWifi.TRIGGER_WIFI)) wifiActivator.removeTrigger(t);
        if (t.getName().equals(TriggerSMS.TRIGGER_SMS)) smsActivator.removeTrigger(t);
        manageRegistrations();
    }

    /**
     * Used to register the broadcast receivers on the system only
     * when needed.
     */
    private void manageRegistrations() {
        for (BroadcastActivator ba : activators) {
            if (ba.getTriggersCount() == 0 && ba.isRegistered())
                ba.unregister();
            else if (ba.getTriggersCount() > 0 && !ba.isRegistered()) {
                ba.register();
            }
        }
    }

    /**
     * Helper method to create a new intent filter.
     *
     * @param actions an array containing the actions of
     *                this intent filter
     * @return a new IntentFilter instance.
     */
    public static IntentFilter newIntentFilter(String[] actions) {
        IntentFilter iF = new IntentFilter();
        for (String s : actions) {
            iF.addAction(s);
        }
        return iF;
    }
}
