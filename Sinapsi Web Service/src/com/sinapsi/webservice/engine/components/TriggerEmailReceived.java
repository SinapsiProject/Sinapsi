package com.sinapsi.webservice.engine.components;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.sinapsi.engine.Event;
import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.parameters.FormalParamBuilder;

public class TriggerEmailReceived extends Trigger {
    public static final String TRIGGER_EMAIL_RECEIVED = "TRIGGER_EMAIL_RECEIVED";
    
    @Override
    public String getName() {
        return TRIGGER_EMAIL_RECEIVED;
    }

    @Override
    public int getMinVersion() {
       return SinapsiVersions.ANTARES.ordinal();
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return null;
    }

    @Override
    public JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                        .putAdvancedString("message", true)
                        .putAdvancedString("subject", true)
                        .create();
    }

    @Override
    protected JSONObject extractParameterValues(Event e, ExecutionInterface di) {
        //TODO: ask giuseppe
        return null;
    }

}
