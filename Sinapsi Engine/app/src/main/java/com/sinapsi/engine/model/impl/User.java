package com.sinapsi.engine.model.impl;

import com.sinapsi.engine.model.UserInterface;

/**
 * Implementation of the user interface
 *
 * Created by Ayoub on 17/04/15.
 */
public class User implements UserInterface {
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
