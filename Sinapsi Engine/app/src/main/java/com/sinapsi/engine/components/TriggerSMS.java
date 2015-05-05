package com.sinapsi.engine.components;

import com.sinapsi.engine.Event;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.system.SystemFacade;
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

    public static final String TRIGGER_SMS = "TRIGGER_SMS";


    /**
     * Default ctor, needed by ComponentLoader to create an instance
     * with java reflection.
     * DO NOT DIRECTLY CALL THIS: THIS SHOULD BE CALLED ONLY BY
     * ComponentLoader. USE ComponentFactory TO CREATE A NEW INSTANCE
     * INSTEAD.
     */
    public TriggerSMS(){
        super();
    }


    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException {
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
        return 1;
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(SystemFacade.REQUIREMENT_SMS_READ, 1)
                .create();
    }
}
