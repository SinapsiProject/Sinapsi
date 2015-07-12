package com.sinapsi.engine.builder;

import com.sinapsi.android.Lol;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.parameters.ActualParamBuilder;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.TriggerDescriptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: doku
 */
public class TriggerBuilder {

    private boolean invalid = false;

    private String name;
    private int deviceId;
    private List<ParameterBuilder> parameters = new ArrayList<>();


    public TriggerBuilder(Trigger trigger) {
        debuildTrigger(trigger);
    }

    public TriggerBuilder(TriggerDescriptor trigger, int deviceId){
        debuildTriggerDescriptor(trigger, deviceId);
    }

    private void debuildTriggerDescriptor(TriggerDescriptor trigger, int deviceId) {
        this.name = trigger.getName();
        this.deviceId = deviceId;

        try {
            JSONObject formalJson = new JSONObject(trigger.getFormalParameters());
            JSONArray formalPArray = formalJson.getJSONArray(FormalParamBuilder.FORMAL_PARAMETERS);

            JSONObject actualJson = new JSONObject(); //this is from a descriptor, so a new empty actual param obj is ok

            for (int i = 0; i < formalPArray.length(); ++i) {
                JSONObject fo = formalPArray.getJSONObject(i);
                ParameterBuilder pm = new ParameterBuilder(fo, actualJson);
                this.parameters.add(pm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void debuildTrigger(Trigger trigger) {
        this.name = trigger.getName();
        this.deviceId = trigger.getExecutionDevice().getId();

        try {
            JSONObject formalJson = trigger.getFormalParametersJSON();
            JSONArray formalPArray = formalJson.getJSONArray(FormalParamBuilder.FORMAL_PARAMETERS);

            JSONObject actualJson = new JSONObject(trigger.getActualParameters()).getJSONObject(ActualParamBuilder.PARAMETERS);

            for (int i = 0; i < formalPArray.length(); ++i) {
                JSONObject fo = formalPArray.getJSONObject(i);
                ParameterBuilder pm = new ParameterBuilder(fo, actualJson);
                this.parameters.add(pm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Trigger buildTrigger(ComponentFactory cf, MacroInterface m) {
        if(name.equals(ComponentFactory.TRIGGER_EMPTY)) return cf.newEmptyTrigger(m);
        String params = ParameterBuilder.buildActualString(parameters);
        return cf.newTrigger(name, params, m, deviceId);
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

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }
}
