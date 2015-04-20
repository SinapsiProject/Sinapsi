package com.sinapsi.model.components;

import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.parameters.ConnectionStatusChoices;
import com.sinapsi.model.parameters.SwitchStatusChoices;
import com.sinapsi.engine.system.WifiAdapter;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.engine.Event;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.Trigger;
import com.sinapsi.model.parameters.FormalParamBuilder;
import com.sinapsi.utils.HashMapBuilder;
import com.sinapsi.utils.SinapsiJSONUtils;

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

    public static final int TRIGGER_WIFI_ID = 1;

    public static final String TRIGGER_WIFI = "TRIGGER_WIFI";

    /**
     * Creates a new TriggerWifi instance.
     * @param executionDevice the device on which this trigger is activated
     * @param parameters the JSON string containing the actual parameters
     * @param macro the macro which is going to be activated by this trigger
     */
    public TriggerWifi(DeviceInterface executionDevice, String parameters, MacroInterface macro){
        super(executionDevice, parameters, macro);
    }


    @Override
    public int getId() {
        return TRIGGER_WIFI_ID;
    }

    @Override
    public String getName() {
        return TRIGGER_WIFI;
    }

    @Override
    public int getMinVersion() {
        return 1;
    }


    @Override
    protected JSONObject extractParameterValues(Event e, DeviceInterface di) throws JSONException {
        WifiAdapter wa = (WifiAdapter) di.getSystemFacade().getSystemService(SystemFacade.SERVICE_WIFI);
        return new JSONObject()
                .put("wifi_status", wa.getStatus().toString())
                .put("wifi_connection_status", wa.getConnectionStatus().toString())
                .put("wifi_ssid", wa.getSSID());
    }

    @Override
    public HashMap<String,Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(SystemFacade.REQUIREMENT_WIFI, 1)
                .create();
    }

    @Override
    protected JSONObject getFormalParametersJSON() throws JSONException{
        return new FormalParamBuilder()
                .put("wifi_status", SinapsiJSONUtils.enumValuesToJSONArray(SwitchStatusChoices.class), true)
                .put("wifi_connection_status", SinapsiJSONUtils.enumValuesToJSONArray(ConnectionStatusChoices.class), true)
                .put("wifi_ssid", FormalParamBuilder.Types.STRING, true)
                .create();

    }

}
