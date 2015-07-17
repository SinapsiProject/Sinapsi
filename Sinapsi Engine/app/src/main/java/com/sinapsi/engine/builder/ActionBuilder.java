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
public class ActionBuilder {

    private ComponentBuilderValidityStatus validity = ComponentBuilderValidityStatus.VALID;

    private String name;
    private int deviceId;
    private List<ParameterBuilder> parameters = new ArrayList<>();

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
                if(ad == null) {
                    validity = ComponentBuilderValidityStatus.INVALID_MISSING_COMPONENT;
                }
                else debuildAction(ad, remoteDeviceId, a.getActualParameters());
            }
        }
    }

    public ActionBuilder(ActionDescriptor a, int deviceId){
        debuildAction(a, deviceId);
    }




    private void debuildAction(Action action) {
        this.name = action.getName();
        this.deviceId = action.getExecutionDevice().getId();

        try {
            JSONObject formalJson = action.getFormalParametersJSON();


            JSONArray formalPArray = formalJson.getJSONArray(FormalParamBuilder.FORMAL_PARAMETERS);

            JSONObject actualJson = new JSONObject(action.getActualParameters()).getJSONObject(ActualParamBuilder.PARAMETERS);

            for (int i = 0; i < formalPArray.length(); ++i) {
                JSONObject fo = formalPArray.getJSONObject(i);
                ParameterBuilder pm = new ParameterBuilder(fo, actualJson);
                this.parameters.add(pm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void debuildAction(ActionDescriptor action, int deviceId) {
        this.name = action.getName();
        this.deviceId = deviceId;

        try {
            JSONObject formalJson = new JSONObject(action.getFormalParameters());
            JSONArray formalPArray = formalJson.getJSONArray(FormalParamBuilder.FORMAL_PARAMETERS);

            JSONObject actualJson = new JSONObject(); //this is from a descriptor representing a new inserted action,
                                                        // so a new empty actual param obj is ok


            for (int i = 0; i < formalPArray.length(); ++i) {
                JSONObject fo = formalPArray.getJSONObject(i);
                ParameterBuilder pm = new ParameterBuilder(fo, actualJson);
                this.parameters.add(pm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void debuildAction(ActionDescriptor action, int deviceId, String actualParams) {
        this.name = action.getName();
        this.deviceId = deviceId;

        try {
            JSONObject formalJson = new JSONObject(action.getFormalParameters());
            JSONArray formalPArray = formalJson.getJSONArray(FormalParamBuilder.FORMAL_PARAMETERS);

            JSONObject actualJson = new JSONObject(actualParams).getJSONObject(ActualParamBuilder.PARAMETERS);


            for (int i = 0; i < formalPArray.length(); ++i) {
                JSONObject fo = formalPArray.getJSONObject(i);
                ParameterBuilder pm = new ParameterBuilder(fo, actualJson);
                this.parameters.add(pm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Action buildAction(ComponentFactory cf) {
        String params = ParameterBuilder.buildActualString(parameters);
        return cf.newAction(name, params, deviceId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public List<ParameterBuilder> getParameters() {
        return parameters;
    }

    public ComponentBuilderValidityStatus getValidity() {
        return validity;
    }

    public void setValidity(ComponentBuilderValidityStatus validity) {
        this.validity = validity;
    }
}
