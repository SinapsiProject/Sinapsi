package com.sinapsi.model.impl;

import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.UserInterface;

import java.util.List;

/**
 * Implementation of the user interface
 *
 */
public class User extends ComunicationInfo implements UserInterface {
    private List<DeviceInterface> devices;
    private int id;
    private String email;
    private String password;

    User(int Id, String mail, String pwd) {
    	super();
    	id = Id;
    	password = pwd;
    	email = mail;
    }

    User(int Id, String mail, String pwd, List<DeviceInterface> devices){
        this(Id, mail, pwd);
        this.devices = devices;
    }
    
    /**
     * Return the id of the user
     * @return id
     */
    @Override
    public int getId() {
        return id;
    }


    /**
     * Return the email of the user
     * @return email
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * Return the password of the user
     * @return password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Set the email
     * @param mail email
     */
    @Override
    public void setEmail(String mail) {
        email = mail;
    }

    /**
     * Set the a password
     * @param pwd password
     */
    @Override
    public void setPassword(String pwd) {
        password = pwd;
    }

    @Override
    public List<DeviceInterface> getDevices() {
        return devices;
    }

    @Override
    public void setDevices(List<DeviceInterface> devices) {
        this.devices = devices;
    }
}
