package com.sinapsi.android.utils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

/**
 * Collection of static methods to perform Intent elaboration or
 * conversions.
 */
public class IntentUtils {

    /**
     * Extracts from a broadcast intent for an incoming SMS all the
     * SMS messages contained in the "pdus" bundle entry, in an
     * 'all-versions-support' way.
     *
     * @param intent the incoming sms broadcast intent
     * @return an array of the messages extracted from the pdus
     * @throws java.lang.RuntimeException if the given intent has null
     *         bundle or null "pdus" entry in the bundle
     */
    public static SmsMessage[] getMessagesFromIntent(Intent intent){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                if(pdusObj == null) throw new RuntimeException("Given intent bundle has null \"pdus\" entry");
                SmsMessage[] messages = new SmsMessage[pdusObj.length];
                for (int i = 0; i < pdusObj.length; i++)
                    messages[i]=SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                return messages;
            } else {// bundle is null
                throw new RuntimeException("Given intent has null bundle");
            }
        } else {
            return Telephony.Sms.Intents.getMessagesFromIntent(intent);
        }
    }
}
