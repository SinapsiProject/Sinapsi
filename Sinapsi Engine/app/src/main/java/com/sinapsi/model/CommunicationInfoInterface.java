package com.sinapsi.model;

/**
 * Comunication error interface.
 * Gives a way to verify if occured an comunication error between server and clients
 *
 */
public interface CommunicationInfoInterface {

    public static final String ERROR_INVALID_CREDENTIALS = "ERROR_INVALID_CREDENTIALS";

    public boolean isErrorOccured();
    public String getErrorDescription();
    public String getAdditionalInfo();
    public void setErrorDescription(String description);
    public void setAdditionalInfo(String info);
    public void errorOccured(boolean error);

}
