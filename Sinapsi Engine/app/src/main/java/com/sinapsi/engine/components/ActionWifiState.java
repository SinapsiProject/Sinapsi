package com.sinapsi.engine.components;

import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.engine.system.WifiAdapter;
import com.sinapsi.model.Action;
import com.sinapsi.model.parameters.FormalParamBuilder;
import com.sinapsi.utils.HashMapBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * ActionWifiState class. This Action will turn on or off the
 * system's default wifi adapter. This action must be parametrized
 * to tell at execution phase if the wifi adapter must be turned on
 * or off.
 * Notice that this action is completely platform-independent:
 * it relies on other facades/adapters like SystemFacade and
 * WifiAdapter.
 */
public class ActionWifiState extends Action{

    public static final int ACTION_WIFI_STATE_ID = 1;

    public static final String ACTION_WIFI_STATE = "ACTION_WIFI_STATE";

    /**
     * Default ctor, needed by ComponentLoader to create an instance
     * with java reflection.
     * DO NOT DIRECTLY CALL THIS: THIS SHOULD BE CALLED ONLY BY
     * ComponentLoader. USE ComponentFactory TO CREATE A NEW INSTANCE
     * INSTEAD.
     */
    public ActionWifiState(){
        super();
    }

    @Override
    public void activate(final ExecutionInterface s) {

        WifiAdapter wa = (WifiAdapter) s.getSystemFacade().getSystemService(SystemFacade.SERVICE_WIFI);
        JSONObject pjo = getParamsObj(params);
        boolean activate;
        try {
            activate = pjo.getBoolean("wifi_switch");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        if (activate) wa.setStatus(true);
        else wa.setStatus(false);

    }

    @Override
    public int getId() {
        return ACTION_WIFI_STATE_ID;
    }

    @Override
    public String getName() {
        return ACTION_WIFI_STATE;
    }

    @Override
    public int getMinVersion() {
        return 1;
    }


    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String,Integer>()
                .put(SystemFacade.REQUIREMENT_WIFI, 1)
                .create();
    }

    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("wifi_switch", FormalParamBuilder.BoolStyles.ON_OFF,false)
                .create();
    }


}
