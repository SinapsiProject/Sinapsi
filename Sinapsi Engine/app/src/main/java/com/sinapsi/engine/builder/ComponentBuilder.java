package com.sinapsi.engine.builder;

import com.sinapsi.engine.Action;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.parameters.ActualParamBuilder;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.impl.TriggerDescriptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: doku
 */
public class ComponentBuilder {

    protected ComponentBuilderValidityStatus validity = ComponentBuilderValidityStatus.VALID;

    protected String name;
    protected int deviceId;

    protected List<ParameterBuilder> parameters = new ArrayList<>();

    protected void debuild(MacroComponent component, int deviceId, String actualParametersString){
        this.name = component.getName();
        this.deviceId = deviceId;

        try {
            if (component.getFormalParameters() != null) {
                JSONObject formalJson;
                if(component instanceof Trigger){
                    formalJson = ((Trigger) component).getFormalParametersJSON();
                } else if (component instanceof Action) {
                    formalJson = ((Action) component).getFormalParametersJSON();
                } else { //this is just a descriptor
                    formalJson = new JSONObject(component.getFormalParameters()); //reconvert from string to jsonobject
                }
                JSONArray formalPArray = formalJson.getJSONArray(FormalParamBuilder.FORMAL_PARAMETERS);

                JSONObject actualJson;
                if(actualParametersString != null)
                    actualJson = new JSONObject(actualParametersString).getJSONObject(ActualParamBuilder.PARAMETERS);
                else
                    actualJson = new JSONObject(); //this is from a descriptor, so a new empty actual param obj is ok

                for (int i = 0; i < formalPArray.length(); ++i) {
                    JSONObject fo = formalPArray.getJSONObject(i);
                    ParameterBuilder pm = new ParameterBuilder(fo, actualJson);
                    this.parameters.add(pm);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<ParameterBuilder> getParameters() {
        return parameters;
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



    public ComponentBuilderValidityStatus getValidity() {
        return validity;
    }

    public void setValidity(ComponentBuilderValidityStatus validity) {
        this.validity = validity;
    }
}
