package com.sinapsi.engine.log;

import java.util.Calendar;
import java.util.Date;

/**
 * LogMessage class. Represents a message in the Sinapsi Log system.
 */
public class LogMessage{

    private Date timestamp = Calendar.getInstance().getTime();
    private String tag;
    private String message;

    /**
     * Ctor. Sets also a timestamp with the time of instantiation.
     * @param tag the tag
     * @param message the message
     */
    public LogMessage(String tag, String message){
        this.tag = tag;
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    //here can be added more info, for example references to components or devices

}
