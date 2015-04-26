package com.sinapsi.model;

/**
 * The default Model Factory Interface
 *
 */
public interface FactoryModelInterface {

    /**
     * Should create a new instance of UserInterface
     *
     * @param id the user id in the db TODO: do we need this?
     * @param email the user's email
     * @param password the user's password
     * @return a new UserInterface instance
     */
    public UserInterface newUser(int id, String email, String password);

    /**
     * Should create a new instance of DeviceInterface
     *
     * @param id the device id in the db
     * @param name the device name i.e. "Office Phone"
     * @param model the model of the device i.e. "Nexus 5"
     * @param type the type of the device i.e. "AndroidSmartphone"
     * @param user the owner of the device
     * @param version the version of the sinapsi client running on a device
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
     * @param id the id of the abstract action
     * @param minVersion min version of the abstract action
     * @param name the name of the abstract action
     * @return
     */
    public ActionInterface newActionAbstraction(int id, int minVersion, String name);
    
    /**
     * Should create a new trigger abstract representation
     * @param id the id of the abstract trigger
     * @param minVersion min version of the abstract trigger
     * @param name the name of the abstract trigger
     * @return
     */
    public TriggerInterface newTriggerAbstraction(int id, int minVersion, String name);
    
}
