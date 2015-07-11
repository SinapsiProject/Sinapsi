package com.sinapsi.engine.builder;

import com.sinapsi.engine.Action;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.model.MacroInterface;

import java.util.ArrayList;
import java.util.List;

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


    public MacroBuilder(MacroInterface inputMacro) {
        this.macro = inputMacro;

        debuildMacro();
    }

    private void debuildMacro() {
        this.name = macro.getName();
        this.iconName = macro.getIconName();
        this.color = macro.getMacroColor();
        this.valid = macro.isValid();
        this.executionFailurePolicy = macro.getExecutionFailurePolicy();
        this.enabled = macro.isEnabled();
        this.trigger = new TriggerBuilder(macro.getTrigger());
        for(Action a: macro.getActions()){
            this.actions.add(new ActionBuilder(a));
        }
    }

    public MacroInterface build(MacroEngine engine){
        macro.setName(name);
        macro.setIconName(iconName);
        macro.setMacroColor(color);
        macro.setValid(valid);
        macro.setExecutionFailurePolicy(executionFailurePolicy);
        macro.setEnabled(enabled);
        macro.setTrigger(trigger.buildTrigger(engine.getComponentFactory(), macro));
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
