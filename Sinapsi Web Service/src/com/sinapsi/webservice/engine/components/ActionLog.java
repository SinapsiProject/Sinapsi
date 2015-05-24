package com.sinapsi.webservice.engine.components;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.execution.ExecutionInterface;

/**
 * This is a serious action that make log 
 *
 */
public class ActionLog extends Action {

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getMinVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void onActivate(ExecutionInterface ei) throws JSONException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException {
        // TODO Auto-generated method stub
        return null;
    }

}
