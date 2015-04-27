package com.sinapsi.engine;

import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;

/**
 * Collection of objects and utilities needed during macro
 * execution. This should be instantiated every time a macro starts,
 * and then the same instance passed within the components of the
 * macro.
 */
public class ExecutionInterface {

    private DeviceInterface device;
    private SystemFacade system;
    private VariableManager globalVars;
    private VariableManager localVars;

    /**
     * Creates a new ExecutionInterface.
     * @param system the system facade of this device (use null for remote devices)
     *           TODO: or use another SF where there is a set of remote calls
     * @param device this device, the one on which the macro is executed.
     */
    public ExecutionInterface(SystemFacade system, DeviceInterface device,
                              VariableManager globalVars){
        this.system = system;
        this.device = device;
        this.localVars = new VariableManager();
        this.globalVars = globalVars;
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

    /**
     * Local variable Manager getter
     * @return the variable manager
     */
    public VariableManager getLocalVars(){
        return localVars;
    }

    /**
     * Globals variable Manager getter
     * @return the variable manager
     */
    public VariableManager getGlobalVars(){
        return globalVars;
    }


}
