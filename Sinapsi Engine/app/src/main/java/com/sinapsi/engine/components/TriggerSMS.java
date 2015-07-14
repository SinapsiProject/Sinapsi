package com.sinapsi.engine.components;

import com.sinapsi.engine.Event;
import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.system.SMSAdapter;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.utils.HashMapBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * TriggerSMS class. This trigger will activate a macro when
 * a new SMS message arrives on the device. This trigger can
 * be parametrized by the sender or/and by the content of the
 * message.
 */
public class TriggerSMS extends Trigger{

    public static final String TRIGGER_SMS = "TRIGGER_SMS";

    @Override
    public JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("sender_number", FormalParamBuilder.Types.STRING, true)
                .putAdvancedString("message_content", true)
                .create();
    }

    @Override
    protected JSONObject extractParameterValues(Event e, ExecutionInterface di) throws JSONException {
        return e.getJSONObject();
    }

    @Override
    public String getName() {
        return TRIGGER_SMS;
    }

    @Override
    public int getMinVersion() {
        return SinapsiVersions.ANTARES.ordinal();
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(SMSAdapter.REQUIREMENT_SMS_READ, 1)
                .create();
    }
}
