package com.sinapsi.model;

/**
 * Action interface. This interface must be implemented
 * by every class implementing every different action type.
 * For example, classes like ActionNotification and ActionSMS should
 * implement directly this interface.
 *
 */
public interface Action extends Parameterized, DistributedComponent {

    /**
     * Method to be implemented in order to be called by
     * the engine when an action instance should do his work
     * inside a macro.
     *
     * @param s passed by the MacroEngine, used to give access
     *          to eventual system-dependant calls needed by
     *          the action
     */
    public void activate(SystemFacade s);
}
