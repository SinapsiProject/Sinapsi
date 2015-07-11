package com.sinapsi.model.impl;

import com.sinapsi.engine.Action;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.execution.ActionListExecution;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.model.MacroInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * The macro class.
 * A macro is a list of actions activated by a trigger.
 *
 */
public class Macro extends CommunicationInfo implements MacroInterface {

    public static final String ABORT_ON_UNAVAILABLE_AT_START = "ABORT_ON_UNAVAILABLE_AT_START";
    public static final String ABORT_ON_UNAVAILABLE_AT_SWITCH = "ABORT_ON_UNAVAILABLE_AT_SWITCH";
    public static final String ENQUEUE_CONTINUE_REQUEST = "ENQUEUE_CONTINUE_REQUEST";

    private Trigger trigger;
    private List<Action> actions;
    private String name;
    private int id;

    private String iconName = "ic_macro_default";
    private String iconColor = "#667a7f";
    private boolean valid = true;
    private String failurePolicy = ABORT_ON_UNAVAILABLE_AT_SWITCH;
    private boolean enabled = true;

    /**
     * Macro ctor.
     * @param id the macro's id
     * @param name the macro's name
     */
   Macro(int id, String name){
	    super();
        this.id = id;
        this.name = name;
        actions = new ArrayList<>();
    }

    /**
     * Macro's name getter
     * @return name of the macro
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Macro's id getter
     * @return id of the macro
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Macro's name setter
     * @param nm the macro's name
     */
    @Override
    public void setName(String nm) {
        name = nm;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Macro's action list getter
     * @return the macro's action list
     */
    @Override
    public List<Action> getActions() {
        return actions;
    }

    /**
     * Appends an action to the macro's action list
     * @param a the Action object
     */
    @Override
    public void addAction(Action a) {
        actions.add(a);
    }

    /**
     * Sets the macro's trigger
     * @param t the Trigger object
     */
    @Override
    public void setTrigger(Trigger t) {
        trigger = t;
    }

    /**
     * Macro's trigger getter
     * @return the Trigger of this macro
     */
    @Override
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Macro's icon name getter
     * @return the icon name
     */
    @Override
    public String getIconName() {
        return iconName;
    }

    /**
     * Macro's icon name setter
     * @param iconName the icon name
     */
    @Override
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    /**
     * Macro's accent color getter
     * @return the color
     */
    @Override
    public String getMacroColor() {
        return iconColor;
    }

    /**
     * Macro's accent color setter
     * @param color the color
     */
    @Override
    public void setMacroColor(String color) {
        this.iconColor = color;
    }

    /**
     * Macro validity getter
     * @return the validity
     */
    @Override
    public boolean isValid() {
        return valid;
    }

    /**
     * Macro validity setter
     * @param valid the validity
     */
    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Macro execution failure policy getter
     * @return the policy
     */
    @Override
    public String getExecutionFailurePolicy() {
        return failurePolicy;
    }

    /**
     * Macro execution failure policy setter
     * @param policy the policy
     */
    @Override
    public void setExecutionFailurePolicy(String policy) {
        this.failurePolicy = policy;
    }


    /**
     * Starts the execution of the macro. This method
     * should be called on a separate thread, in order
     * to multiple macros to be executed concurrently.
     * @param sf the execution interface representing
     *           this macro's execution.
     */
    @Override
    public void execute(ExecutionInterface sf) {
        if(!actions.isEmpty())sf.pushScope(new ActionListExecution(actions));
        sf.execute();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        getTrigger().setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
