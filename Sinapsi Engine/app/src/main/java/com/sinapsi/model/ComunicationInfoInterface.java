package com.sinapsi.model;

/**
 * Comunication error interface.
 * Gives a way to verify if occured an comunication error between server and clients
 *
 */
public interface ComunicationInfoInterface {
    public boolean isErrorOccured();
    public String getErrorDescription();
    public String getAdditionalInfo();
    public void setErrorDescription(String description);
    public void setAdditionalInfo(String info);
    public void errorOccured(boolean error);
}
