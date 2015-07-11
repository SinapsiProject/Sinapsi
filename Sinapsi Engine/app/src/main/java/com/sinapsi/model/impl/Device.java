package com.sinapsi.model.impl;

import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.UserInterface;

/**
 * Implementation of the device interface
 *
 */
public class Device extends CommunicationInfo implements DeviceInterface {
    private int id;
    private int version;
    private String name;
    private String model;
    private String type;
    private UserInterface user;


    /**
     * Device ctor.
     * @param id the Sinapsi id of this device
     * @param name the device's chosen name (i.e. "Work phone")
     * @param model the device's model (i.e. "Nexus 5")
     * @param type the device type (i.e. "AndroidSmartphone")
     * @param clientVersion the version of the Sinapsi client installed on this device
     * @param user the owner of this device
     */
    Device(int id, String name, String model, String type, int clientVersion, UserInterface user){
    	super();
        this.id = id;
        this.name = name;
        this.model = model;
        this.type = type;
        this.version = clientVersion;
        this.user = user;
    }

    /**
     * Return the id of the device
     * @return id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Return the current version of sinapsi installed in the device
     * @return device
     */
    @Override
    public int getVersion() {
        return version;
    }

    /**
     * Return the name of the device
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Return the model of the device
     * @return model
     */
    @Override
    public String getModel() {
        return model;
    }

    /**
     * Return the type of the device
     * @return
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Return the user of the device
     * @return
     */
    @Override
    public UserInterface getUser() {
        return user;
    }

    /**
     * Set a new name for the device
     * @param nm name
     */
    @Override
    public void setName(String nm) {
        name = nm;
    }

    /**
     * Set a new model for the device
     * @param mod model
     */
    @Override
    public void setModel(String mod) {
        model = mod;
    }

    /**
     * Set a new type for the device
     * @param tp type
     */
    @Override
    public void setType(String tp) {
        type = tp;
    }

    /**
     * Set a new user for the device
     * @param us user
     */
    @Override
    public void setUser(UserInterface us) {
        user = us;
    }

    /**
     * Set a new version for the device
     * @param ver version
     */
    @Override
    public void setVersion(int ver) {
        version = ver;
    }

}
