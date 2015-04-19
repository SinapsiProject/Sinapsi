package com.sinapsi.model.impl;

import com.sinapsi.model.Action;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.Trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * The macro class.
 * A macro is a list of actions activated by a trigger.
 *
 */
public class Macro implements MacroInterface {
    private Trigger trigger;
    private List<Action> actions;
    private String name;
    private int id;

    /**
     * Macro ctor.
     * @param id the macro's id
     * @param name the macro's name
     */
    public Macro(int id, String name){
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
     * Starts the execution of the macro. This method
     * should be called on a separate thread, in order
     * to multiple macros to be executed concurrently.
     * @param sf the device where the macro starts the
     *           execution.
     */
    @Override
    public void execute(DeviceInterface sf) {
        //TODO: create the concept of "macro execution" and implement
        //TODO: it in a class. Could be useful for async actions, either
        //TODO: to stop and restart the execution of the macro.
        for(Action a: actions){
            a.activate(sf);
        }
    }
}
