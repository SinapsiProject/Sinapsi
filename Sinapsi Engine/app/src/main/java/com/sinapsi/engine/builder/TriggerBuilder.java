package com.sinapsi.engine.builder;

import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.parameters.ActualParamBuilder;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.ComponentsAvailability;
import com.sinapsi.model.impl.TriggerDescriptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO: doku
 */
public class TriggerBuilder extends ComponentBuilder{


    public TriggerBuilder(int currentDeviceid, Map<Integer, ComponentsAvailability> availabilityMap, Trigger trigger) {
        if(trigger.getExecutionDevice().getId() == currentDeviceid){
            debuildTrigger(trigger);
        }else{
            int remoteDeviceId = trigger.getExecutionDevice().getId();
            ComponentsAvailability ca = availabilityMap.get(remoteDeviceId);
            if(ca == null) {
                validity = ComponentBuilderValidityStatus.INVALID_MISSING_DEVICE;
                this.name = trigger.getName();
                this.deviceId = remoteDeviceId;
            }
            else{
                TriggerDescriptor td = ca.getTriggers().get(trigger.getName());
                if(td == null) validity = ComponentBuilderValidityStatus.INVALID_MISSING_COMPONENT;
                else debuildTrigger(td, remoteDeviceId, trigger.getActualParameters());
            }
        }
    }

    public TriggerBuilder(TriggerDescriptor trigger, int deviceId){
        debuildTrigger(trigger, deviceId);
    }

    private void debuildTrigger(TriggerDescriptor trigger, int deviceId, String actualParams) {
        super.debuild(trigger, deviceId, actualParams);
    }

    private void debuildTrigger(TriggerDescriptor trigger, int deviceId) {
        super.debuild(trigger, deviceId, null);
    }

    private void debuildTrigger(Trigger trigger) {
        super.debuild(trigger, trigger.getExecutionDevice().getId(), trigger.getActualParameters());
    }

    public Trigger buildTrigger(ComponentFactory cf, MacroInterface m) {
        if(name.equals(ComponentFactory.TRIGGER_EMPTY)) return cf.newEmptyTrigger(m);
        String params = ParameterBuilder.buildActualString(parameters);
        return cf.newTrigger(name, params, m, deviceId);
    }


}
