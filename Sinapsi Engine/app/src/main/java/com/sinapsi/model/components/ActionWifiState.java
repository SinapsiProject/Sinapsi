package com.sinapsi.model.components;

import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.engine.system.WifiAdapter;
import com.sinapsi.model.Action;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.parameters.SwitchStatusChoices;
import com.sinapsi.utils.HashMapBuilder;

import org.json.JSONArray;
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
public class ActionWifiState implements Action{

    public static final int ACTION_WIFI_STATE_ID = 1;

    public static final String ACTION_WIFI_STATE = "ACTION_WIFI_STATE";

    private DeviceInterface executionDevice;
    private String params;

    /**
     * Creates a new ActionWifiState instance.
     * @param executionDevice the device on wich this action is going to be executed
     * @param params the JSON string containing the actual parameters
     */
    public ActionWifiState(DeviceInterface executionDevice, String params){
        this.executionDevice = executionDevice;
        this.params = params;
    }

    @Override
    public void activate(DeviceInterface s) {
        //if s is the execution device of this action instance
        if(executionDevice.getId() == s.getId()) {
            WifiAdapter wa = (WifiAdapter) s.getSystemFacade().getSystemService(SystemFacade.SERVICE_WIFI);
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
                e1.printStackTrace();
                return;
            }
            boolean activate;
            try {
                activate = pjo.getBoolean("wifi_switch");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            if (activate) wa.setStatus(SwitchStatusChoices.ENABLED);
            else wa.setStatus(SwitchStatusChoices.DISABLED);
        }else{
            //if s is not the execution device of this action instance
            //TODO: remote action
        }
    }

    @Override
    public DeviceInterface getExecutionDevice() {
        return executionDevice;
    }

    @Override
    public int getId() {
        return 1;
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
    public String getFormalParameters() {
        JSONObject result = null;
        try{
            result = new JSONObject().put("formal_parameters", new JSONArray()

                    .put(new JSONObject()
                            .put("name", "wifi_switch")
                            .put("type", "boolean_on_off")
                            .put("optional", false)));
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
