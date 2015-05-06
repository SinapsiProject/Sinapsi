package com.sinapsi.engine;

import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.MacroInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Reflection-based component factory. Used by MacroEditor/MacroBuilder
 * to build new macros. This give methods to get all available components
 * on specific devices, and to create new component instances.
 */
public class ComponentFactory {

    private ComponentLoader loader;
    private DeviceInterface device;
    private SinapsiLog log;

    /**
     * Creates a new component factory.
     * @param device the device
     * @param componentClasses the component classes
     */
    public ComponentFactory(DeviceInterface device, SinapsiLog log, Class<? extends MacroComponent>... componentClasses) {
        this.loader = new ComponentLoader(componentClasses);
        this.log = log;
        this.device = device;
        loader.loadClasses();
        log.log("COMPFACTORY", "Component classes loaded");
    }

    /**
     * Creates a new Trigger instance
     * @param triggerName the name of this trigger
     * @param parameters the actual parameters JSON string (use ActualParamBuilder)
     * @param macro the macro that will be started by this trigger
     * @return a new trigger instance
     */
    public Trigger newTrigger(String triggerName, String parameters, MacroInterface macro){
        Trigger t = (Trigger) loader.newComponentInstance(MacroComponent.ComponentTypes.TRIGGER, triggerName);
        if (t == null) throw new ComponentNotFoundException(triggerName, MacroComponent.ComponentTypes.TRIGGER);
        t.init(device,parameters,macro);
        return t;
    }

    /**
     * Creates a new Action instance
     * @param actionName the name of this action
     * @param parameters the actual parameters JSON string (use ActualParamBuilder)
     * @return a new action instance
     */
    public Action newAction(String actionName, String parameters){
        Action a = (Action) loader.newComponentInstance(MacroComponent.ComponentTypes.ACTION, actionName);
        if(a == null) throw new ComponentNotFoundException(actionName, MacroComponent.ComponentTypes.ACTION);
        a.init(device,parameters);
        return a;
    }

    /**
     * Used to get if a specific component meets the requirements needed
     * to be available on a specific system facade.
     * @param componentName the component name
     * @param componentType the component type
     * @param di the system facade
     * @return true if the component is available on the device, false
     *         otherwise
     */
    public boolean getAvailabilityOnDevice(String componentName, MacroComponent.ComponentTypes componentType, SystemFacade di){
        MacroComponent c = loader.newComponentInstance(componentType, componentName);
        if (c == null) throw new ComponentNotFoundException(componentName, componentType);
        return di.checkRequirements(c);
    }

    /**
     * Get a list of all trigger names available on a specific system facade.
     * @param di the system facade
     * @return a List of String names
     */
    public List<String> getAvailableTriggerNamesOnDevice(SystemFacade di){
        List<String> result = new ArrayList<>();
        for(String x: loader.getTriggerKeys()){
            if(getAvailabilityOnDevice(x, MacroComponent.ComponentTypes.TRIGGER,di))
                result.add(x);
        }
        return result;
    }

    /**
     * Get a list of all action names available on a specific device.
     * @param di the system facade
     * @return a List of String names
     */
    public List<String> getAvailableActionNamesOnDevice(SystemFacade di){
        List<String> result = new ArrayList<>();
        for(String x: loader.getActionKeys()){
            if(getAvailabilityOnDevice(x, MacroComponent.ComponentTypes.ACTION,di))
                result.add(x);
        }
        return result;
    }



}
