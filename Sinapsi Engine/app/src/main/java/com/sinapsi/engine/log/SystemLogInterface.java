package com.sinapsi.engine.log;

/**
 * Interface to be implemented in order to write log messages
 * to the system's output (ie. standard output or a file in
 * java SE, Logcat in Android, or a DB)
 */
public interface SystemLogInterface{

    /**
     * Prints a message.
     * @param lm the message
     */
    public void printMessage(LogMessage lm);
}