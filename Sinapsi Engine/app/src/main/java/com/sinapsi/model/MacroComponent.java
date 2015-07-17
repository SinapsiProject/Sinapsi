package com.sinapsi.model;

import java.util.HashMap;

/**
 * MacroComponent interface
 * Every part of a macro (i.e. triggers and actions)
 * should implement this interface.
 *
 */
public interface MacroComponent extends Parameterized{

    //TODO: could this contain executionDevice field painlessly?

    /**
     * Getter of the metadata name of the class implementing
     * MacroComponent (i.e. TriggerWifi should have a name similar to
     * "TRIGGER_WIFI").
     *
     * @return the name of this component class
     */
    public String getName();

    /**
     * Method needed to specify the minimum version of every Sinapsi
     * client/server software connected to the Sinapsi network of a User,
     * in order for the component to be available
     *
     * @return the minimum version of the client/server on which this
     * component can be used.
     */
    public int getMinVersion();

    /**
     * Method to needed to specify the type of the component
     * (i.e. a Trigger or an Action).
     * @return this component's type
     */
    public ComponentTypes getComponentType();

    /**
     * Method to be implemented by each class of MacroComponent,
     * needed to specify what are the system requirements the
     * device has to meet for the MacroComponent to be used.
     *
     * @return a Map of requirements: each key represents
     * a system feature on the device, and the integer is the minimum
     * value the system has to meet in order to make this component
     * available.
     */
    public HashMap<String,Integer> getSystemRequirementKeys();

    public enum ComponentTypes{
        TRIGGER,
        ACTION
    }
}
