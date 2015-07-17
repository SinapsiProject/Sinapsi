package com.sinapsi.model;

import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.TriggerDescriptor;

import java.util.List;

/**
 * The default Model Factory Interface
 *
 */
public interface FactoryModelInterface {

    /**
     * Should create a new instance of UserInterface
     *
     * @param id the user id in the db
     * @param email the user's email
     * @param password the user's password
     * @return a new UserInterface instance
     */
    public UserInterface newUser(int id, String email, String password, boolean active, String role);

    
    /**
     * Should create a new instance of DeviceInterface
     *
     * @param id the device id in the db
     * @param name the device name i.e. "Office Phone"
     * @param model the model of the device i.e. "Nexus 5"
     * @param type the type of the device i.e. "AndroidSmartphone"
     * @param user the owner of the device
     * @param clientVersion the version of the sinapsi client running on a device
     * @return a new DeviceInterface instance
     */
    public DeviceInterface newDevice(int id, String name, String model, String type, UserInterface user, int clientVersion);

    /**
     * Should create a new instance of MacroInterface
     *
     * @param name the name chosen for the macro
     * @param id the id of the macro in the db
     * @return a new MacroInterface instance
     */
    public MacroInterface newMacro(String name, int id);
    
    /**
     * Should create a new action abstract representation
     * @param minVersion min version of the abstract action
     * @param name the name of the abstract action
     * @return
     */
    public ActionDescriptor newActionDescriptor(int minVersion, String name, String formalParameters);
    
    /**
     * Should create a new trigger abstract representation
     * @param minVersion min version of the abstract trigger
     * @param name the name of the abstract trigger
     * @return
     */
    public TriggerDescriptor newTriggerDescriptor(int minVersion, String name, String formalParameters);
    
}
