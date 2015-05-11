package com.sinapsi.model;

import java.util.List;

/**
 * User interface.
 * Pure model support interface for the User class.
 *
 */
public interface UserInterface {
    public int getId();
    public String getEmail();
    public String getPassword();
    
    public void setEmail(String email);
    public void setPassword(String password);

    List<DeviceInterface> getDevices();

    void setDevices(List<DeviceInterface> devices);
}
