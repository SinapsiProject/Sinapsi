package com.sinapsi.model;

/**
 * User interface
 *
 */
public interface UserInterface {
    public int getId();
    public String getEmail();
    public String getPassword();
    
    public void setEmail(String email);
    public void setPassword(String password);
}
