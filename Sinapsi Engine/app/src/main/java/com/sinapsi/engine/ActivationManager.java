package com.sinapsi.engine;

import com.sinapsi.model.Trigger;

/**
 * Interface to allow those triggers that are registered
 * to for system events to be notified for activation.
 * Every platform should implement this interface.
 */
public interface ActivationManager {

    //TODO: manage distribution

    /**
     * Registers a trigger for activation.
     * @param t the trigger
     */
    public void addToNotifyList(Trigger t);

    /**
     * Unregisters the specified trigger.
     * @param t the trigger
     */
    public void removeFromNotifyList(Trigger t);
}
