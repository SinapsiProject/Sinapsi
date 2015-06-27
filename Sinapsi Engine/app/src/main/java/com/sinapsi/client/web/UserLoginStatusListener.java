package com.sinapsi.client.web;

import com.sinapsi.model.UserInterface;

/**
 * Interface used to notify interested classes of
 * successful user logins/logouts from Sinapsi web
 * service.
 */
public interface UserLoginStatusListener {
    public void onUserLogIn(UserInterface user);

    public void onUserLogOut();
}
