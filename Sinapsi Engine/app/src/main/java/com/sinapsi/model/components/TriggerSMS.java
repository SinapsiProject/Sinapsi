package com.sinapsi.model.components;

import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.engine.Event;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.Trigger;
import com.sinapsi.model.parameters.FormalParamBuilder;
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

    public static final int TRIGGER_SMS_ID = 2;
    public static final String TRIGGER_SMS = "TRIGGER_SMS";

    /**
     * Creates a new TriggerSMS instance.
     * @param executionDevice the device on which this trigger is activated
     * @param parameters the JSON string containing the actual parameters
     * @param macro the macro which is going to be activated by this trigger
     */
    public TriggerSMS(DeviceInterface executionDevice, String parameters, MacroInterface macro) {
        super(executionDevice, parameters, macro);
    }


    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("sender_number", FormalParamBuilder.Types.STRING, true)
                .putAdvancedString("message_content", true)
                .create();
    }

    @Override
    protected JSONObject extractParameterValues(Event e, DeviceInterface di) throws JSONException {
        return e.getJSONObject();
    }

    @Override
    public int getId() {
        return TRIGGER_SMS_ID;
    }

    @Override
    public String getName() {
        return TRIGGER_SMS;
    }

    @Override
    public int getMinVersion() {
        return 1;
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(SystemFacade.REQUIREMENT_SMS_READ, 1)
                .create();
    }
}
