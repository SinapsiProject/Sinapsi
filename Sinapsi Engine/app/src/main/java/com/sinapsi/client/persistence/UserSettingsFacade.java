package com.sinapsi.client.persistence;

import com.sinapsi.model.UserInterface;

/**
 * UserSettingsFacade interface. This interface contains a collection
 * of methods useful to interact with persistent user settings.
 */
public interface UserSettingsFacade {

    //TODO: key saving and management for auto login

    /**
     * This method will return the saved user.
     *
     * @return the user
     */
    public UserInterface getSavedUser();

    /**
     * Save the user on the settings file.
     *
     * @param u the user to be saved
     */
    public void saveUser(UserInterface u);

}
