package com.sinapsi.engine.builder;

import com.sinapsi.client.AppConsts;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.ComponentsAvailability;
import com.sinapsi.model.impl.Macro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is an helper class used to register all the user's changes
 * on a specified macro and build it only when editing is completed.
 */
public class MacroBuilder{


    private MacroInterface macro;

    private String name;
    private String iconName;
    private String color;
    private boolean valid;
    private String executionFailurePolicy;
    private boolean enabled;
    private TriggerBuilder trigger;
    private List<ActionBuilder> actions = new ArrayList<>();


    public MacroBuilder(int currentDeviceId, Map<Integer, ComponentsAvailability> availabilityMap, MacroInterface inputMacro) {
        this.macro = inputMacro;

        debuildMacro(currentDeviceId, availabilityMap);
    }

    private void debuildMacro(int currentDeviceId, Map<Integer, ComponentsAvailability> availabilityMap) {
        this.name = macro.getName();
        this.iconName = macro.getIconName();
        this.color = macro.getMacroColor();
        this.valid = macro.isValid();
        this.executionFailurePolicy = macro.getExecutionFailurePolicy();
        this.enabled = macro.isEnabled();
        this.trigger = new TriggerBuilder(currentDeviceId, availabilityMap, macro.getTrigger());
        for(Action a: macro.getActions()){
            this.actions.add(new ActionBuilder(currentDeviceId, availabilityMap, a));
        }
    }

    public boolean validate(){
        valid = internalValidate();
        return valid;
    }

    private boolean internalValidate(){
        if(name == null || name.isEmpty()) return false;
        if(iconName==null || iconName.isEmpty()) iconName = AppConsts.DEFAULT_MACRO_ICON;
        if(color == null || color.isEmpty()) color = AppConsts.DEFAULT_MACRO_COLOR;
        if(executionFailurePolicy == null || executionFailurePolicy.isEmpty()) executionFailurePolicy = Macro.ABORT_ON_UNAVAILABLE_AT_SWITCH;
        if(trigger == null || trigger.getName().equals(ComponentFactory.TRIGGER_EMPTY)) return false;
        if(trigger.getValidity() != ComponentBuilderValidityStatus.VALID) return false;
        if(trigger.getName() == null || trigger.getName().isEmpty()) return false;
        for(ActionBuilder ab: actions){
            if(ab.getName() == null || ab.getName().isEmpty()) return false;
            if(ab.getValidity() != ComponentBuilderValidityStatus.VALID) return false;
        }
        return true;
    }

    public MacroInterface build(MacroEngine engine){
        macro.setName(name);
        macro.setIconName(iconName);
        macro.setMacroColor(color);
        macro.setValid(valid);
        macro.setExecutionFailurePolicy(executionFailurePolicy);
        macro.setEnabled(enabled);

        macro.setTrigger(trigger.buildTrigger(engine.getComponentFactory(), macro));

        macro.getActions().clear();
        for(ActionBuilder ab: actions){

            macro.addAction(ab.buildAction(engine.getComponentFactory()));
        }
        return macro;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getExecutionFailurePolicy() {
        return executionFailurePolicy;
    }

    public void setExecutionFailurePolicy(String executionFailurePolicy) {
        this.executionFailurePolicy = executionFailurePolicy;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TriggerBuilder getTrigger() {
        return trigger;
    }

    public void setTrigger(TriggerBuilder trigger) {
        this.trigger = trigger;
    }

    public List<ActionBuilder> getActions() {
        return actions;
    }


}
