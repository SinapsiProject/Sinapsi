package com.sinapsi.model;

import com.sinapsi.engine.system.SystemFacade;

/**
 * Trigger interface. This interface must be implemented
 * by every class implementing every different type of trigger.
 * For example, classes like TriggerWifi and TriggerSMS should
 * implement this interface.
 *
 */
public interface Trigger extends Parameterized, DistributedComponent {

    /**
     * Method to be implemented in order to be called when a
     * system event regarding the type of trigger occurs. This
     * method should then make a parameter check, and inform the
     * engine on success.
     * @param e Event object containing infos about the system event
     *          that activated this trigger
     * @param di Device object to give a way to access device
     *           and system main infos and services
     */
    public void onActivate(Event e, DeviceInterface di);

    //TODO: add setEnabled(boolean,ActivationManager);
}
