package com.sinapsi.model.impl;

import com.sinapsi.model.DeviceInterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class containing all the infos about components availability
 * and device relationships in a map.
 */
public class AvailabilityMap extends CommunicationInfo implements Iterable<ComponentsAvailability>{

    private Map<Integer, ComponentsAvailability> map = new HashMap<>();

    public AvailabilityMap(){}

    public AvailabilityMap(Map<Integer, ComponentsAvailability> map) {
        this.map = map;
    }

    public void put(DeviceInterface device, List<TriggerDescriptor> triggers, List<ActionDescriptor> actions){
        this.map.put(device.getId(), new ComponentsAvailability(device, triggers, actions));
    }

    public ComponentsAvailability get(int deviceId){
        return this.map.get(deviceId);
    }

    public DeviceInterface getDevice(int deviceId){
        if(this.map.get(deviceId) == null) return null;
        return this.map.get(deviceId).getDevice();
    }

    public void clear(){
        this.map.clear();
    }


    @Override
    public Iterator<ComponentsAvailability> iterator() {
        return this.map.values().iterator();
    }
}
