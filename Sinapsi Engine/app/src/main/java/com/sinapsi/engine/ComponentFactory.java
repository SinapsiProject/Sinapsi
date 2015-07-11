package com.sinapsi.engine;

import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.model.impl.TriggerDescriptor;

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
    private FactoryModel fm = new FactoryModel();

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
    public Trigger newTrigger(String triggerName, String parameters, MacroInterface macro, int executionDeviceId){
        if(device.getId() == executionDeviceId){
            Trigger t = (Trigger) loader.newComponentInstance(MacroComponent.ComponentTypes.TRIGGER, triggerName);
            if (t == null) {
                throw new ComponentNotFoundException(triggerName, MacroComponent.ComponentTypes.TRIGGER);
            }
            t.init(device, parameters, macro);
            return t;
        }
            else return newRemoteTrigger(triggerName, parameters, macro, executionDeviceId);
    }

    /**
     * Creates a new Action instance
     * @param actionName the name of this action
     * @param parameters the actual parameters JSON string (use ActualParamBuilder)
     * @return a new action instance
     */
    public Action newAction(String actionName, String parameters, int executionDeviceId){
        if(device.getId() == executionDeviceId){
            Action a = (Action) loader.newComponentInstance(MacroComponent.ComponentTypes.ACTION, actionName);
            if(a == null){
                throw new ComponentNotFoundException(actionName, MacroComponent.ComponentTypes.ACTION);
            }
            a.init(device, parameters);
            return a;

        }
        else return newRemoteAction(actionName, parameters, executionDeviceId);
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
     * @return a List of TriggerDescriptor object containing all the metadata of
     *          available triggers.
     */
    public List<TriggerDescriptor> getAvailableTriggerDescriptors(SystemFacade di){
        List<TriggerDescriptor> result = new ArrayList<>();
        for(String name: loader.getTriggerKeys()){
            if(getAvailabilityOnDevice(name, MacroComponent.ComponentTypes.TRIGGER,di))
                result.add(newTriggerDescriptor(name));
        }
        return result;
    }

    /**
     * Get a list of all action names available on a specific device.
     * @param di the system facade
     * @return a List of String names
     */
    public List<ActionDescriptor> getAvailableActionDescriptors(SystemFacade di){
        List<ActionDescriptor> result = new ArrayList<>();
        for(String name: loader.getActionKeys()){
            if(getAvailabilityOnDevice(name, MacroComponent.ComponentTypes.ACTION,di))
                result.add(newActionDescriptor(name));
        }
        return result;
    }

    private ActionDescriptor newActionDescriptor(String name){
        Action a = (Action) loader.newComponentInstance(MacroComponent.ComponentTypes.ACTION, name);
        return fm.newActionDescriptor(a.getMinVersion(), a.getName(), a.getFormalParameters());
    }

    private TriggerDescriptor newTriggerDescriptor(String name){
        Trigger t = (Trigger) loader.newComponentInstance(MacroComponent.ComponentTypes.TRIGGER, name);
        return fm.newTriggerDescriptor(t.getMinVersion(), t.getName(), t.getFormalParameters());
    }

    public Trigger newEmptyTrigger(MacroInterface macro){
        Trigger t = new Trigger() {
            @Override
            public JSONObject getFormalParametersJSON() throws JSONException {
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

    private Trigger newRemoteTrigger(final String componentName, String parameters, MacroInterface macro, int executionDevice){
        Trigger t = new Trigger() {
            @Override
            public JSONObject getFormalParametersJSON() throws JSONException {
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

        t.init(fm.newDevice(
                executionDevice,
                "",
                "",
                "",
                device.getUser(),//HINT: manage better this when social aspects will be implemented
                -1), parameters, macro);
        return t;

    }

    private Action newRemoteAction(final String componentName, String parameters, int executionDevice){
        Action a = new Action() {
            @Override
            protected void onActivate(ExecutionInterface ei) throws JSONException {
                //does nothing
            }

            @Override
            public JSONObject getFormalParametersJSON() throws JSONException {
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

        a.init(fm.newDevice(
                executionDevice,
                "",
                "",
                "",
                device.getUser(),//HINT: manage better this when social aspects will be implemented
                -1),
                parameters);

        return a;
    }





}
