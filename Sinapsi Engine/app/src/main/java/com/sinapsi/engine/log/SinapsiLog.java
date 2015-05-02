package com.sinapsi.engine.log;


import java.util.ArrayList;
import java.util.List;

/**
 * Class used to write and keep in memory log messages relative to
 * the engine or the execution of macro.
 */
public class SinapsiLog {


    private List<LogMessage> messages = new ArrayList<>();
    private List<SystemLogInterface> logInterfaces = new ArrayList<>();

    /**
     * Adds a log interface to the list. From now on new log messages are
     * printed in the specified interface as well.
     * @param sli the log interface
     */
    public void addLogInterface(SystemLogInterface sli){
        logInterfaces.add(sli);
    }

    /**
     * Puts a new message in the log, with the tag "SINAPSI"
     * @param message the message
     */
    public void log(String message){
        log("SINAPSI", message);
    }

    /**
     * Puts a new message in the log, with the specified tag
     * and message.
     * @param tag the tag
     * @param message the message
     */
    public void log(String tag, String message){
        LogMessage lm = new LogMessage(tag, message);
        messages.add(lm);
        for(SystemLogInterface sli: logInterfaces){
            sli.printMessage(lm);
        }
    }

    /**
     * Gets a list of all messages since the start of the logging system.
     * @return messages
     */
    public List<LogMessage> getMessages(){
        return  messages;
    }

}
