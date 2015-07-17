package com.sinapsi.model.impl;

import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.TriggerDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the sets of available actions and triggers on a specific device
 */
public class ComponentsAvailability {

    private DeviceInterface device;
    private Map<String, TriggerDescriptor> triggers = new HashMap<>();
    private Map<String, ActionDescriptor> actions = new HashMap<>();

    public ComponentsAvailability(DeviceInterface device,
                                  List<TriggerDescriptor> triggers,
                                  List<ActionDescriptor> actions){
        this.device = device;

        for(TriggerDescriptor t : triggers){
            this.triggers.put(t.getName(), t);
        }

        for(ActionDescriptor a : actions){
            this.actions.put(a.getName(), a);
        }

    }

    public DeviceInterface getDevice() {
        return device;
    }

    public Map<String, TriggerDescriptor> getTriggers() {
        return triggers;
    }

    public Map<String, ActionDescriptor> getActions() {
        return actions;
    }
}
