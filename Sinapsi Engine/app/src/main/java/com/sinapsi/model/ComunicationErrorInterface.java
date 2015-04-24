package com.sinapsi.model;

/**
 * Comunication error interface.
 * Gives a way to verify if occured an comunication error between server and clients
 *
 */
public interface ComunicationErrorInterface {
    public boolean isErrorOccured();
    public String getErrorDescription();
    public void setErrorDescription(String description);
    public void errorOccured(boolean error);
}
