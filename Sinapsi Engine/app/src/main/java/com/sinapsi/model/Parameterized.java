package com.sinapsi.model;

/**
 * Parameterized interface.
 * Every class that needs to indicate formal parameters
 * for its instantiation (i.e. when a new trigger for a macro is defined)
 * or needs to get and extract actual parameters at
 * "execution time" (i.e. when a trigger activates)
 * should implement this interface.
 *
 */
public interface Parameterized {
    public String getFormalParameters();
    public String getActualParameters();
    public void setActualParameters(String params);
}
