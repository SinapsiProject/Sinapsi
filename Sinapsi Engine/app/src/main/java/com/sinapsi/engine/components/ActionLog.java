package com.sinapsi.engine.components;

import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.parameters.FormalParamBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * ActionLog class. This Action will put a message in the Sinapsi
 * log system.
 * This action must be parametrized to know at execution phase
 * the log message.
 * Notice that this action is completely platform-independent:
 * it relies on other facades/adapters in ExecutionInterface.
 */
public class ActionLog extends Action {

    public static final String ACTION_LOG = "ACTION_LOG";

    @Override
    public void onActivate(ExecutionInterface di) throws JSONException{
        JSONObject pjo = getParsedParams(di.getLocalVars(),di.getGlobalVars());
        String message = null;

        message = pjo.getString("log_message");

        di.getLog().log("LOG_ACTION", message);
    }

    @Override
    public JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("log_message", FormalParamBuilder.Types.STRING, false)
                .create();
    }



    @Override
    public String getName() {
        return ACTION_LOG;
    }

    @Override
    public int getMinVersion() {
        return SinapsiVersions.ANTARES.ordinal();
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return null; //NO REQUIREMENTS NEEDED. Integrated in engine.
        //This means this action is always available, on
        // every device.
    }
}
