package com.sinapsi.android.system;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsMessage;

import com.sinapsi.android.utils.IntentUtils;
import com.sinapsi.engine.ActivationManager;
import com.sinapsi.engine.Event;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.components.TriggerSMS;
import com.sinapsi.engine.components.TriggerWifi;
import com.sinapsi.engine.system.SystemFacade;

/**
 * ActivationManager - implementation for the Android platform.
 * This handles the trigger activation using android-exclusive
 * mechanisms like BroadcastReceivers.
 */
public class AndroidActivationManager extends ActivationManager {

    private ContextWrapper contextWrapper;

    private BroadcastActivator wifiActivator = null;

    private BroadcastActivator smsActivator = null;

    private BroadcastActivator[] activators = new BroadcastActivator[]{
            wifiActivator,
            smsActivator
    };

    /**
     * Creates a new AndroidActivationManager instance with the specified
     * ContextWrapper.
     *
     * @param cw the contextWrapper
     *
     */
    public AndroidActivationManager(ContextWrapper cw, SystemFacade sf) {
        this.contextWrapper = cw;


        if(sf.checkRequirement(SystemFacade.REQUIREMENT_WIFI, 1)) wifiActivator = new BroadcastActivator(
                this,
                newIntentFilter(new String[]{
                        "android.net.wifi.STATE_CHANGE",
                        "android.net.wifi.WIFI_STATE_CHANGED"
                }), contextWrapper, executionInterface) {
            @Override
            public Event extractEventInfo(Context c, Intent i) {
                return null;
            }
        };


        if(sf.checkRequirement(SystemFacade.REQUIREMENT_SMS_READ, 1)) smsActivator = new BroadcastActivator(
                this,
                newIntentFilter(new String[]{
                        "android.provider.Telephony.SMS_RECEIVED"
                }), contextWrapper, executionInterface) {
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
    }

    @Override
    public void addToNotifyList(Trigger t) {
        super.addToNotifyList(t);

        if (t.getName().equals(TriggerWifi.TRIGGER_WIFI) && wifiActivator!= null) wifiActivator.addTrigger(t);
        if (t.getName().equals(TriggerSMS.TRIGGER_SMS) && smsActivator!= null) smsActivator.addTrigger(t);

        manageRegistrations();
    }

    @Override
    public void removeFromNotifyList(Trigger t) {
        super.removeFromNotifyList(t);

        if (t.getName().equals(TriggerWifi.TRIGGER_WIFI) && wifiActivator!= null) wifiActivator.removeTrigger(t);
        if (t.getName().equals(TriggerSMS.TRIGGER_SMS) && smsActivator!= null) smsActivator.removeTrigger(t);

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
