package com.sinapsi.engine.components;

import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.model.Action;
import com.sinapsi.model.parameters.FormalParamBuilder;

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
    public static final int ACTION_LOG_ID = 6;

    @Override
    public void activate(ExecutionInterface di) {
        JSONObject pjo = getParamsObj(params);
        String message = null;
        try{
            message = pjo.getString("log_message");
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }
        di.getLog().log("LOG_ACTION", message);
    }

    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("log_message", FormalParamBuilder.Types.STRING, false)
                .create();
    }

    @Override
    public int getId() {
        return ACTION_LOG_ID;
    }

    @Override
    public String getName() {
        return ACTION_LOG;
    }

    @Override
    public int getMinVersion() {
        return 1;
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return null; //NO REQUIREMENTS NEEDED. Integrated in engine.
        //This means this action is always available, on
        // every device.
    }
}
