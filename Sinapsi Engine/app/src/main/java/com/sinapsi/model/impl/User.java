package com.sinapsi.model.impl;

import com.sinapsi.model.UserInterface;

import java.util.List;

/**
 * Implementation of the user interface
 *
 */
public class User extends ComunicationError implements UserInterface {
    private List<Device> devices;
    private int id;
    private String email;
    private String password;

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
}
