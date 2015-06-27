package com.sinapsi.engine;

import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.MacroInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static final String TRIGGER_EMPTY = "TRIGGER_EMPTY";

    /**
     * Creates a new component factory.
     * @param device the device
     * @param componentClasses the component classes
     */
    @SafeVarargs
    public ComponentFactory(DeviceInterface device, SinapsiLog log, Class<? extends MacroComponent>... componentClasses) {
        this.loader = new ComponentLoader(componentClasses);
        this.log = log;
        this.device = device;
        loader.loadClasses();
        this.log.log("COMPFACTORY", "Component classes loaded");
        if (this.device == null)throw new RuntimeException("device is null");
    }

    /**
     * Creates a new Trigger instance
     * @param triggerName the name of this trigger
     * @param parameters the actual parameters JSON string (use ActualParamBuilder)
     * @param macro the macro that will be started by this trigger
     * @return a new trigger instance
     */
    public Trigger newTrigger(String triggerName, String parameters, MacroInterface macro, DeviceInterface executionDevice){
        Trigger t = (Trigger) loader.newComponentInstance(MacroComponent.ComponentTypes.TRIGGER, triggerName);
        if (t == null) {
            if(device.getId() == executionDevice.getId())
                throw new ComponentNotFoundException(triggerName, MacroComponent.ComponentTypes.TRIGGER);
            else return newRemoteTrigger(triggerName, parameters, macro, executionDevice);
        }
        t.init(executionDevice, parameters, macro);
        return t;
    }

    /**
     * Creates a new Action instance
     * @param actionName the name of this action
     * @param parameters the actual parameters JSON string (use ActualParamBuilder)
     * @return a new action instance
     */
    public Action newAction(String actionName, String parameters, DeviceInterface executionDevice){
        Action a = (Action) loader.newComponentInstance(MacroComponent.ComponentTypes.ACTION, actionName);
        if(a == null){
            if(device.getId() == executionDevice.getId())
                throw new ComponentNotFoundException(actionName, MacroComponent.ComponentTypes.ACTION);
            else return newRemoteAction(actionName, parameters, executionDevice);
        }
        a.init(executionDevice, parameters);
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

    public Trigger newEmptyTrigger(MacroInterface macro){
        Trigger t = new Trigger() {
            @Override
            protected JSONObject getFormalParametersJSON() throws JSONException {
                return null;
            }

            @Override
            protected JSONObject extractParameterValues(Event e, ExecutionInterface di) throws JSONException {
                return null;
            }

            @Override
            public String getName() {
                return TRIGGER_EMPTY;
            }

            @Override
            public int getMinVersion() {
                return 0;
            }

            @Override
            public HashMap<String, Integer> getSystemRequirementKeys() {
                return null;
            }
        };

        t.init(device, "", macro);
        return t;
    }

    private Trigger newRemoteTrigger(final String componentName, String parameters, MacroInterface macro, DeviceInterface executionDevice){
        Trigger t = new Trigger() {
            @Override
            protected JSONObject getFormalParametersJSON() throws JSONException {
                return null;
            }

            @Override
            protected JSONObject extractParameterValues(Event e, ExecutionInterface di) throws JSONException {
                return null;
            }

            @Override
            public String getName() {
                return componentName;
            }

            @Override
            public int getMinVersion() {
                return 0;
            }

            @Override
            public HashMap<String, Integer> getSystemRequirementKeys() {
                return null;
            }
        };

        t.init(executionDevice, parameters, macro);
        return t;

    }

    private Action newRemoteAction(final String componentName, String parameters, DeviceInterface executionDevice){
        Action a = new Action() {
            @Override
            protected void onActivate(ExecutionInterface ei) throws JSONException {
                //does nothing
            }

            @Override
            protected JSONObject getFormalParametersJSON() throws JSONException {
                return null;
            }

            @Override
            public String getName() {
                return componentName;
            }

            @Override
            public int getMinVersion() {
                return 0;
            }

            @Override
            public HashMap<String, Integer> getSystemRequirementKeys() {
                return null;
            }
        };

        a.init(executionDevice,parameters);

        return a;
    }





}
