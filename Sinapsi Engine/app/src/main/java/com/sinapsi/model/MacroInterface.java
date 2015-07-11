package com.sinapsi.model;

import com.sinapsi.engine.Action;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.execution.ExecutionInterface;

import java.util.List;

/**
 * Macro interface.
 * Pure model support interface for the Macro class.
 *
 */
public interface MacroInterface extends CommunicationInfoInterface {
    public String getName();
    public int getId();
    public void setName(String name);
    public void setId(int id);

    public List<Action> getActions();
    public void addAction(Action a);

    public void setTrigger(Trigger t);
    public Trigger getTrigger();

    public String getIconName();
    public void setIconName(String iconName);

    public String getMacroColor();
    public void setMacroColor(String color);

    public boolean isValid();
    public void setValid(boolean valid);

    public String getExecutionFailurePolicy();
    public void setExecutionFailurePolicy(String policy);

    public void execute(ExecutionInterface fs);

    public void setEnabled(boolean enabled);
    public boolean isEnabled();
}
