package com.sinapsi.engine.builder;

import com.sinapsi.engine.Action;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.parameters.ActualParamBuilder;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.ComponentsAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO: doku
 */
public class ActionBuilder extends ComponentBuilder{



    public ActionBuilder(int currentDeviceid, Map<Integer, ComponentsAvailability> availabilityMap, Action a) {
        if(a.getExecutionDevice().getId() == currentDeviceid){
            debuildAction(a);
        }else{
            int remoteDeviceId = a.getExecutionDevice().getId();
            ComponentsAvailability ca = availabilityMap.get(remoteDeviceId);
            if(ca == null) {
                validity = ComponentBuilderValidityStatus.INVALID_MISSING_DEVICE;
                this.name = a.getName();
                this.deviceId = remoteDeviceId;
            }
            else{
                ActionDescriptor ad = ca.getActions().get(a.getName());
                if(ad == null) validity = ComponentBuilderValidityStatus.INVALID_MISSING_COMPONENT;
                else debuildAction(ad, remoteDeviceId, a.getActualParameters());
            }
        }
    }

    public ActionBuilder(ActionDescriptor a, int deviceId){
        debuildAction(a, deviceId);
    }


    private void debuildAction(Action action) {
        super.debuild(action, action.getExecutionDevice().getId(), action.getActualParameters());
    }


    private void debuildAction(ActionDescriptor action, int deviceId) {
        super.debuild(action, deviceId, null);
    }

    private void debuildAction(ActionDescriptor action, int deviceId, String actualParams) {
        super.debuild(action, deviceId, actualParams);
    }

    public Action buildAction(ComponentFactory cf) {
        String params = ParameterBuilder.buildActualString(parameters);
        return cf.newAction(name, params, deviceId);
    }

}
