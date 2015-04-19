package com.sinapsi.model.components;

import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.parameters.ConnectionStatusChoices;
import com.sinapsi.model.parameters.SwitchStatusChoices;
import com.sinapsi.engine.system.WifiAdapter;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.Event;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.Trigger;
import com.sinapsi.utils.HashMapBuilder;
import com.sinapsi.utils.SinapsiJSONUtils;

import org.json.JSONArray;
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
public class TriggerWifi implements Trigger {

    public static final int TRIGGER_WIFI_ID = 1;

    public static final String TRIGGER_WIFI = "TRIGGER_WIFI";

    private DeviceInterface executionDevice;
    private String params;
    private MacroInterface macro;

    /**
     * Creates a new TriggerWifi instance.
     * @param executionDevice the device on which this trigger is activated
     * @param parameters the JSON string containing the actual parameters
     * @param macro the macro which is going to be activated by this trigger
     */
    public TriggerWifi(DeviceInterface executionDevice, String parameters, MacroInterface macro){
        this.executionDevice = executionDevice;
        this.params = parameters;
        this.macro = macro;
    }

    @Override
    public void onActivate(Event e, DeviceInterface di) {
        SystemFacade s = di.getSystemFacade();
        if (!s.checkRequirements(this))
            throw new RuntimeException("Requirements not met at execution phase");//TODO: new exception class?

        WifiAdapter wa = (WifiAdapter) s.getSystemService(SystemFacade.SERVICE_WIFI);

        //checks the parameters
        //TODO: automate this process in another method (probably Trigger should be promoted to abstract class)
        JSONObject o = null;
        try {
            o = new JSONObject(params);
        } catch (JSONException e1) {
            //actual parameters string is not well-formed
            e1.printStackTrace();
            return;
        }
        JSONObject pjo;
        try {
            pjo = o.getJSONObject("parameters");
        } catch (JSONException e1) {
            /*
            there is no parameters array, and, because all trigger parameters
            are optional, and are meant to filter the events, this means 'just
            activate the macro every time (i.e. either when wifi sets on and off)
            */
            e1.printStackTrace();
            macro.execute(di);
            return;
        }
        if (!pjo.optString("wifi_status").equals(wa.getStatus().toString())&&!pjo.optString("wifi_status").isEmpty()){
            return;
        }
        if (!pjo.optString("wifi_connection_status").equals(wa.getConnectionStatus().toString())&&!pjo.optString("wifi_connection_status").isEmpty()){
            return;
        }
        if (!pjo.optString("wifi_ssid").equals(wa.getSSID())&&!pjo.optString("wifi_ssid").isEmpty()){
            return;
        }
        //if all parameters are met or are null, start the macro
        macro.execute(di);


    }

    @Override
    public DeviceInterface getExecutionDevice() {
        return executionDevice;
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
    public HashMap<String,Integer> getSystemRequirementKeys() {
        return new HashMapBuilder<String, Integer>()
                .put(SystemFacade.REQUIREMENT_WIFI, 1)
                .create();
    }

    @Override
    public String getFormalParameters() {
        JSONObject result = null;
        try {
            result = new JSONObject().put("formal_parameters", new JSONArray()

                    .put(new JSONObject()
                            .put("name", "wifi_status")
                            .put("type", "choice")
                            .put("choiceEntries", SinapsiJSONUtils.enumValuesToJSONArray(SwitchStatusChoices.class))
                            .put("optional", true)) //by default all trigger parameters are optional

                    .put(new JSONObject()
                            .put("name", "wifi_connection_status")
                            .put("type", "choice")
                            .put("choiceEntries", SinapsiJSONUtils.enumValuesToJSONArray(ConnectionStatusChoices.class))
                            .put("optional", true))

                    .put(new JSONObject()
                            .put("name", "wifi_ssid")
                            .put("type", "string")
                            .put("optional", true)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result!=null ? result.toString() : null;
    }

    @Override
    public String getActualParameters() {
        return params;
    }

    @Override
    public void setActualParameters(String params) {
        this.params = params;
    }
}
