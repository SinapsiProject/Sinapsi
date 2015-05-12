package com.sinapsi.engine.components;

import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.system.SMSAdapter;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.utils.HashMapBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * ActionSendSMS class. This Action will send a new sms.
 * This action must be parametrized to know at execution phase
 * the text message body and the recipient number
 * Notice that this action is completely platform-independent:
 * it relies on other facades/adapters like SystemFacade and
 * SMSAdapter.
 */
public class ActionSendSMS extends Action {


    public static final String ACTION_SEND_SMS = "ACTION_SEND_SMS";

    @Override
    public void onActivate(final ExecutionInterface di) throws JSONException{
        SMSAdapter sa = (SMSAdapter) di.getSystemFacade().getSystemService(SystemFacade.SERVICE_SMS);
        JSONObject pjo = getParsedParams(di.getLocalVars(),di.getGlobalVars());
        SMSAdapter.Sms sms = new SMSAdapter.Sms();

        sms.setAddress(pjo.getString("number"));
        sms.setMsg(pjo.getString("msg"));

        sa.sendSMSMessage(sms);//TODO: check returned boolean
    }

    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("number", FormalParamBuilder.Types.STRING, false)
                .put("msg", FormalParamBuilder.Types.STRING, false)
                .create();
    }

    @Override
    public String getName() {
        return ACTION_SEND_SMS;
    }

    @Override
    public int getMinVersion() {
        return SinapsiVersions.ANTARES.ordinal();
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(SystemFacade.REQUIREMENT_SMS_SEND, 1)
                .create();
    }
}
