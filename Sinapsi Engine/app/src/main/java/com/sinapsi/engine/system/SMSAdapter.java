package com.sinapsi.engine.system;

import java.util.List;

/**
 * Interface used to adapt various system calls
 * to send or read SMSs.
 */
public interface SMSAdapter{

    public static final String SERVICE_SMS = "SERVICE_SMS";
    public static final String REQUIREMENT_SMS_READ = "REQUIREMENT_SMS_READ";
    public static final String REQUIREMENT_SMS_SEND = "REQUIREMENT_SMS_SEND";

    /**
     * Pure SMS-related info container
     */
    public class Sms{
        private String _id;
        private String _address;
        private String _msg;
        private String _readState; //"0" for have not read sms and "1" for have read sms
        private String _time;

        public String getId(){
            return _id;
        }
        public String getAddress(){
            return _address;
        }
        public String getMsg(){
            return _msg;
        }
        public String getReadState(){
            return _readState;
        }
        public String getTime(){
            return _time;
        }

        public void setId(String id){
            _id = id;
        }
        public void setAddress(String address){
            _address = address;
        }
        public void setMsg(String msg){
            _msg = msg;
        }
        public void setReadState(String readState){
            _readState = readState;
        }
        public void setTime(String time){
            _time = time;
        }

    }


    /**
     * Sends a new sms message
     * @param message the message.
     * @return true on success, false on fail.
     */
    public boolean sendSMSMessage(Sms message);

    /**
     * Get all sms messages on the phone's inbox
     * @return the list of sms messages
     */
    public List<Sms> getInboxMessages();

    /**
     * Get all sms messages on the phone's sent folder
     * @return the list of sms messages
     */
    public List<Sms> getSentMessages();
}
