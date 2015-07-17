package com.sinapsi.model.impl;

import com.sinapsi.model.UserInterface;

/**
 * Implementation of the user interface
 *
 */
public class User extends CommunicationInfo implements UserInterface {

    private int id;
    private String role;
    private String email;
    private String password;
    private boolean activation;

    User(int Id, String mail, String pwd, boolean active, String userRole) {
    	super();
    	id = Id;
    	password = pwd;
    	email = mail;
    	activation = active;
    	role = userRole;
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
     * Return the role of the user
     * @return String
     */
    @Override
    public String getRole() {
        return role;
    }
    
    /**
     * Return the value of the user activation
     */
    @Override
    public boolean getActivation() {
        return activation;
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
}
