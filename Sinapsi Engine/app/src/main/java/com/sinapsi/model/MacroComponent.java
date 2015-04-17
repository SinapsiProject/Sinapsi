package com.sinapsi.model;

import java.util.List;

/**
 * Macro component interface
 *
 */
public interface MacroComponent {

    /**
     * The integer Id chosen for this type of Component.
     * This should have the same id in the database
     *
     * @return the id
     */
    public int getId();

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
     * Method to be implemented by each class of MacroComponent,
     * needed to specify what are the system requirements the
     * device has to meet for the MacroComponent to be used.
     *
     * @return a List of String keys: each key represents
     * a system feature on the device.
     */
    public List<String> getSystemRequirementKeys();
}
