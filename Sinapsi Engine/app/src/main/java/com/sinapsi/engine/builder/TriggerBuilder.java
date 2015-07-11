package com.sinapsi.engine.builder;

import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.parameters.ActualParamBuilder;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.model.MacroInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: doku
 */
public class TriggerBuilder {

    private String name;
    private int deviceId;
    private List<ParameterBuilder> parameters = new ArrayList<>();


    public TriggerBuilder(Trigger trigger) {
        debuildTrigger(trigger);
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

    public void setParameters(List<ParameterBuilder> parameters) {
        this.parameters = parameters;
    }
}
