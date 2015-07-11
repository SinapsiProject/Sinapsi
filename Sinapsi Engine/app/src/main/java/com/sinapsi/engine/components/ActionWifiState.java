package com.sinapsi.engine.components;

import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.system.WifiAdapter;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.parameters.FormalParamBuilder;
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


    public static final String ACTION_WIFI_STATE = "ACTION_WIFI_STATE";

    @Override
    public void onActivate(final ExecutionInterface s) throws JSONException{

        WifiAdapter wa = (WifiAdapter) s.getSystemFacade().getSystemService(WifiAdapter.SERVICE_WIFI);
        JSONObject pjo = getParsedParams(s.getLocalVars(),s.getGlobalVars());
        boolean activate;

        activate = pjo.getBoolean("wifi_switch");

        if (activate) wa.setStatus(true);
        else wa.setStatus(false);

    }

    @Override
    public String getName() {
        return ACTION_WIFI_STATE;
    }

    @Override
    public int getMinVersion() {
        return SinapsiVersions.ANTARES.ordinal();
    }


    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String,Integer>()
                .put(WifiAdapter.REQUIREMENT_WIFI, 1)
                .create();
    }

    @Override
    public JSONObject getFormalParametersJSON() throws JSONException {
        return new FormalParamBuilder()
                .put("wifi_switch", FormalParamBuilder.BoolStyles.ON_OFF,false)
                .create();
    }


}
