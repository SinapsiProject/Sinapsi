package com.sinapsi.engine.components;

import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.system.NotificationAdapter;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.Action;
import com.sinapsi.model.parameters.FormalParamBuilder;
import com.sinapsi.utils.HashMapBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * ActionSimpleNotification class. This Action will show a
 * notification message.
 * This action must be parametrized to know at execution phase
 * the text of th message and the title.
 * Notice that this action is completely platform-independent:
 * it relies on other facades/adapters like SystemFacade and
 * NotificationAdapter.
 */
public class ActionSimpleNotification extends Action {

    public static final String ACTION_SIMPLE_NOTIFICATION = "ACTION_SIMPLE_NOTIFICATION";

    @Override
    public void activate(ExecutionInterface di) {
        JSONObject pjo = getParamsObj(params);
        String title = null;
        String message = null;
        try{
            title = pjo.getString("notification_title");
            message = pjo.getString("notification_message");
        }catch(JSONException e){
            e.printStackTrace();
            return;
        }
        ((NotificationAdapter) di.getSystemFacade().getSystemService(SystemFacade.SERVICE_NOTIFICATION)).showSimpleNotification(title,message);
    }

    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("notification_title", FormalParamBuilder.Types.STRING, false)
                .put("notification_message", FormalParamBuilder.Types.STRING, false)
                .create();
    }

    @Override
    public String getName() {
        return ACTION_SIMPLE_NOTIFICATION;
    }

    @Override
    public int getMinVersion() {
        return 1;
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(SystemFacade.REQUIREMENT_SIMPLE_NOTIFICATIONS, 1)
                .create();
    }
}
