package com.sinapsi.engine;

import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;

/**
 * Collection of objects and utilities needed during macro
 * execution.
 */
public class ExecutionInterface {

    private DeviceInterface device;
    private SystemFacade system;

    /**
     * Creates a new ExecutionInterface.
     * @param system the system facade of this device (use null for remote devices)
     *           TODO: or use another SF where there is a set of remote calls
     * @param device this device, the one on which the macro is executed.
     */
    public ExecutionInterface(SystemFacade system, DeviceInterface device){
        this.system = system;
        this.device = device;
    }

    /**
     * Device getter
     * @return the device
     */
    public DeviceInterface getDevice(){
        return device;
    }

    /**
     * System facade getter
     * @return the system facade
     */
    public SystemFacade getSystemFacade(){
        return system;
    }

    //TODO add variables system here


}
