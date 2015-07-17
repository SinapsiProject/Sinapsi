package com.sinapsi.model;


/**
 * User interface.
 * Pure model support interface for the User class.
 *
 */
public interface UserInterface {
    public int getId();
    public String getEmail();
    public String getPassword();
    public String getRole();
    public boolean getActivation();
    
    public void setEmail(String email);
    public void setPassword(String password);


}
