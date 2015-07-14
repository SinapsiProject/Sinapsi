package com.sinapsi.android.enginesystem;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsMessage;

import com.sinapsi.android.Lol;
import com.sinapsi.android.utils.IntentUtils;
import com.sinapsi.engine.ActivationManager;
import com.sinapsi.engine.Event;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.components.TriggerACPower;
import com.sinapsi.engine.components.TriggerSMS;
import com.sinapsi.engine.components.TriggerScreenPower;
import com.sinapsi.engine.components.TriggerWifi;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.system.CommonDeviceConsts;
import com.sinapsi.engine.system.SMSAdapter;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.engine.system.WifiAdapter;

/**
 * ActivationManager - implementation for the Android platform.
 * This handles the trigger activation using android-exclusive
 * mechanisms like BroadcastReceivers.
 */
public class AndroidActivationManager extends ActivationManager {

    private BroadcastActivator wifiActivator = null;
    private BroadcastActivator smsActivator = null;
    private BroadcastActivator screenPowerActivator = null;
    private BroadcastActivator acPowerActivator = null;

    private BroadcastActivator[] activators = null;

    /**
     * Creates a new AndroidActivationManager instance with the specified
     * ContextWrapper and SystemFacade.
     *
     * @param contextWrapper the contextWrapper
     * @param sf the SystemFacade, used for requirement checks
     */
    public AndroidActivationManager(ExecutionInterface defaultExecutionInterface,
                                    ContextWrapper contextWrapper,
                                    SystemFacade sf) {
        super(defaultExecutionInterface);
        if(sf.checkRequirement(WifiAdapter.REQUIREMENT_WIFI, 1)) wifiActivator = new BroadcastActivator(
                this, newIntentFilter(
                "android.net.wifi.STATE_CHANGE",
                "android.net.wifi.WIFI_STATE_CHANGED"),
                contextWrapper, executionInterface) {
            @Override
            public Event extractEventInfo(Context c, Intent i) {
                return null;
            }
        };


        if(sf.checkRequirement(SMSAdapter.REQUIREMENT_SMS_READ, 1)) smsActivator = new BroadcastActivator(
                this, newIntentFilter(
                "android.provider.Telephony.SMS_RECEIVED"),
                contextWrapper, executionInterface) {
            @Override
            public Event extractEventInfo(Context c, Intent intent) {
                try {
                    SmsMessage[] messages = IntentUtils.getMessagesFromIntent(intent);
                    //HINT: google why an sms intent may contain more messages
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

        if(sf.checkRequirement(CommonDeviceConsts.REQUIREMENT_INTERCEPT_SCREEN_POWER, 1)) screenPowerActivator = new BroadcastActivator(
                this, newIntentFilter(
                Intent.ACTION_SCREEN_OFF,
                Intent.ACTION_SCREEN_ON),
                contextWrapper, executionInterface) {
            @Override
            public Event extractEventInfo(Context c, Intent intent) {
                Event result = new Event();
                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) result.put("screen_power", false);
                else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) result.put("screen_power", true);
                return result;
            }
        };

        if(sf.checkRequirement(CommonDeviceConsts.REQUIREMENT_AC_CHARGER, 1)) acPowerActivator = new BroadcastActivator(
                this, newIntentFilter(
                Intent.ACTION_POWER_CONNECTED,
                Intent.ACTION_POWER_DISCONNECTED),
                contextWrapper, executionInterface) {
            @Override
            public Event extractEventInfo(Context c, Intent intent) {
                Event result = new Event();
                if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) result.put("ac_power", false);
                else if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) result.put("ac_power", true);
                return result;
            }
        };

        activators = new BroadcastActivator[]{
                wifiActivator,
                smsActivator,
                screenPowerActivator,
                acPowerActivator
        };

    }

    @Override
    public void addToNotifyList(Trigger t) {
        super.addToNotifyList(t);

        if (t.getName().equals(TriggerWifi.TRIGGER_WIFI) && wifiActivator!= null) wifiActivator.addTrigger(t);
        if (t.getName().equals(TriggerSMS.TRIGGER_SMS) && smsActivator!= null) smsActivator.addTrigger(t);
        if (t.getName().equals(TriggerScreenPower.TRIGGER_SCREEN_POWER) && screenPowerActivator!= null) screenPowerActivator.addTrigger(t);
        if (t.getName().equals(TriggerACPower.TRIGGER_AC_POWER) && acPowerActivator != null) acPowerActivator.addTrigger(t);

        manageRegistrations();
    }

    @Override
    public void removeFromNotifyList(Trigger t) {
        super.removeFromNotifyList(t);

        if (t.getName().equals(TriggerWifi.TRIGGER_WIFI) && wifiActivator!= null) wifiActivator.removeTrigger(t);
        if (t.getName().equals(TriggerSMS.TRIGGER_SMS) && smsActivator!= null) smsActivator.removeTrigger(t);
        if (t.getName().equals(TriggerScreenPower.TRIGGER_SCREEN_POWER) && screenPowerActivator!= null) screenPowerActivator.removeTrigger(t);
        if (t.getName().equals(TriggerACPower.TRIGGER_AC_POWER) && acPowerActivator != null) acPowerActivator.removeTrigger(t);

        manageRegistrations();
    }


    /**
     * Used to register the broadcast receivers on the system only
     * when needed.
     */
    private void manageRegistrations() {
        for (BroadcastActivator ba : activators) {
            if(ba == null) continue;
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
    public static IntentFilter newIntentFilter(String... actions) {
        IntentFilter iF = new IntentFilter();
        for (String s : actions) {
            iF.addAction(s);
        }
        return iF;
    }
}
