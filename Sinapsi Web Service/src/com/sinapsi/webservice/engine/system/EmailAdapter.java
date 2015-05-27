package com.sinapsi.webservice.engine.system;

import com.sinapsi.model.UserInterface;

/**
 * Email adapter that send a email
 * @author Ayoub
 *
 */
public class EmailAdapter {
    private UserInterface user;
    public static final String SERVICE_EMAIL = "SERVICE_EMAIL"; 
    
    public EmailAdapter(UserInterface u){
        this.user = u;
    }
    
    /**
     * Send email
     * @param user user interface
     * @param msg message of the email
     * @param subject subject of the email
     */
    public void sendMailToUser(String msg, String subject) {
        // TODO: impl
    }
}
