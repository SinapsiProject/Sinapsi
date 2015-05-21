package com.sinapsi.model;

import com.sinapsi.engine.Action;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.execution.ExecutionInterface;
import com.sinapsi.model.datatypes.RGBColor;

import java.util.List;

/**
 * Macro interface.
 * Pure model support interface for the Macro class.
 *
 */
public interface MacroInterface {
    public String getName();
    public int getId();
    public void setName(String name);

    public List<Action> getActions();
    public void addAction(Action a);

    public void setTrigger(Trigger t);
    public Trigger getTrigger();

    public String getIconName();
    public void setIconName(String iconName);

    public RGBColor getMacroColor();
    public void setMacroColor(RGBColor color);

    public boolean isValid();
    public void setValid(boolean valid);

    public String getExecutionFailurePolicy();
    public void setExecutionFailurePolicy(String policy);

    public void execute(ExecutionInterface fs);
}
