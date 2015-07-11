package com.sinapsi.engine.components;

import com.sinapsi.engine.Event;
import com.sinapsi.engine.SinapsiVersions;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.system.WifiAdapter;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.parameters.ConnectionStatusChoices;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.engine.parameters.SwitchStatusChoices;
import com.sinapsi.utils.HashMapBuilder;
import com.sinapsi.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * TriggerWifi class. This trigger will activate a macro when
 * the wifi state or the wifi connection state changes. This
 * trigger can be parametrized by the status or by the ssid the
 * wifi is connected to.
 * Notice that this trigger is completely platform-independent:
 * it relies on other facades/adapters like SystemFacade and WifiAdapter.
 */
public class TriggerWifi extends Trigger {

    public static final String TRIGGER_WIFI = "TRIGGER_WIFI";


    @Override
    public String getName() {
        return TRIGGER_WIFI;
    }

    @Override
    public int getMinVersion() {
        return SinapsiVersions.ANTARES.ordinal();
    }


    @Override
    protected JSONObject extractParameterValues(Event e, ExecutionInterface di) throws JSONException {
        WifiAdapter wa = (WifiAdapter) di.getSystemFacade().getSystemService(WifiAdapter.SERVICE_WIFI);
        return new JSONObject() //FIXME: change, check from Event date (intent extras on android)
                .put("wifi_status", wa.getStatus().toString())
                .put("wifi_connection_status", wa.getConnectionStatus().toString())
                .put("wifi_ssid", wa.getSSID());
    }

    @Override
    public HashMap<String,Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(WifiAdapter.REQUIREMENT_WIFI, 1)
                .create();
    }

    @Override
    public JSONObject getFormalParametersJSON() throws JSONException{
        return new FormalParamBuilder()
                .put("wifi_status", JSONUtils.enumValuesToJSONArray(SwitchStatusChoices.class), true)
                .put("wifi_connection_status", JSONUtils.enumValuesToJSONArray(ConnectionStatusChoices.class), true)
                .put("wifi_ssid", FormalParamBuilder.Types.STRING, true)
                .create();

    }

}
