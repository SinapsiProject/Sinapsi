package com.sinapsi.model.components;

import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.engine.system.WifiAdapter;
import com.sinapsi.model.Action;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.parameters.SwitchStatusChoices;
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
     * Creates a new ActionWifiState instance.
     * @param executionDevice the device on wich this action is going to be executed
     * @param params the JSON string containing the actual parameters
     */
    public ActionWifiState(DeviceInterface executionDevice, String params){
        super(executionDevice, params);
    }

    @Override
    public void activate(DeviceInterface s) {
        //if s is the execution device of this action instance
        if(executionDevice.getId() == s.getId()) {
            WifiAdapter wa = (WifiAdapter) s.getSystemFacade().getSystemService(SystemFacade.SERVICE_WIFI);
            JSONObject pjo = getParamsObj(params);
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
