package com.sinapsi.webservice.engine.components;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.webservice.engine.system.EmailAdapter;

/**
 * Send email Action
 *
 */
public class ActionSendEmail extends Action {
    public static final String ACTION_SEND_EMAIL = "ACTION_SEND_EMAIL";
    
    @Override
    public String getName() {
        return ACTION_SEND_EMAIL;
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
    protected void onActivate(ExecutionInterface ei) throws JSONException {
       EmailAdapter emailAdapter = (EmailAdapter) ei.getSystemFacade().getSystemService(ACTION_SEND_EMAIL);
       JSONObject obj = getParsedParams(ei.getLocalVars(), ei.getGlobalVars());
       emailAdapter.sendMailToUser(obj.getString("message"), obj.getString("subject"));
    }

    @Override
    public JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                    .put("message", FormalParamBuilder.Types.STRING, false)
                    .put("subject", FormalParamBuilder.Types.STRING, false)
                    .create();
    }

}
