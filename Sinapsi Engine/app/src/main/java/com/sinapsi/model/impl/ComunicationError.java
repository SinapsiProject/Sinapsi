package com.sinapsi.model.impl;

import com.sinapsi.model.ComunicationErrorInterface;

/**
 * Implementation of the comunication interface
 *
 */
public class ComunicationError implements ComunicationErrorInterface {

    private String description;
    private boolean errorOccured;

    /**
     * Default ctor
     */
    public ComunicationError() {
        errorOccured = false;
        description = "";
    }

    /**
     * Parametrized ctor
     *
     * @param desc error description
     * @param err true if error occured, false otherwise
     */
    public ComunicationError(String desc, boolean err) {
        description = desc;
        errorOccured = err;
    }

    /**
     * Checks whether an error has occured
     *
     * @return boolean
     */
    @Override
    public boolean isErrorOccured() {
        return errorOccured;
    }

    /**
     * Get the error description
     *
     * @return description
     */
    @Override
    public String getErrorDescription() {
        return description;
    }

    /**
     * Set a new description for the current comunication error
     *
     * @param desc error description
     */
    @Override
    public void setErrorDescription(String desc) {
        description = desc;
    }

    /**
     * Set error variable
     *
     * @param error true if there are errors in comunication, false otherwise
     */
    @Override
    public void errorOccured(boolean error) {
        errorOccured = error;
    }
}
