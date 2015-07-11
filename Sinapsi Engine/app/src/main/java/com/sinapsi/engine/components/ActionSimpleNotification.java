package com.sinapsi.engine.components;

import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.system.NotificationAdapter;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.parameters.FormalParamBuilder;
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
    public void onActivate(ExecutionInterface di) throws JSONException{
        JSONObject pjo = getParsedParams(di.getLocalVars(),di.getGlobalVars());
        String title = null;
        String message = null;

        title = pjo.getString("notification_title");
        message = pjo.getString("notification_message");

        ((NotificationAdapter) di.getSystemFacade().getSystemService(NotificationAdapter.SERVICE_NOTIFICATION)).showSimpleNotification(title,message);
    }

    @Override
    public JSONObject getFormalParametersJSON() throws JSONException {
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
        return SinapsiVersions.ANTARES.ordinal();
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(NotificationAdapter.REQUIREMENT_SIMPLE_NOTIFICATIONS, 1)
                .create();
    }
}
