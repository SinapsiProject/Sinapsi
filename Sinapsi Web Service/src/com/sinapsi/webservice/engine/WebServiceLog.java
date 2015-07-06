package com.sinapsi.webservice.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility Log class for Web Service
 * @author Aleph0
 *
 */
public class WebServiceLog {
    public static final String STANDARD_OUT = "STANDARD_OUT";
    public static final String STANDARD_ERR_OUT = "STANDARD_ERR_OUT";
    public static final String FILE_OUT = "FILE_OUT";
    public static final String WEBSOCKET_FILE_OUT = "WEBSOCKET_FILE_OUT";
    public static final String SERVLET_CONTEXT_FILE_OUT = "SERVLET_CONTEXT_FILE_OUT";
    public static final String ACTION_LOG_FILE = "ACTION_LOG_FILE";
    
    private DateFormat dateFormatLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private BufferedWriter logger;
    
    /**
     * Default ctor. it use standard output
     */
    public WebServiceLog() {
        try {
            logger = new BufferedWriter(openFile(STANDARD_OUT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Seconday ctor. it user a file for output
     * 
     * @param filename
     */
    public WebServiceLog(String type) {
        try {
            logger = new BufferedWriter(openFile(type));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Write the log
     * 
     * @param msg message
     */
    public void log(String msg) {
        try {
            logger.write(msg);
            logger.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Write a tag and the message
     * 
     * @param tag tag of log, maybe the time or other things
     * @param msg message log
     */
    public void log(String tag, String msg) {
        try {
            logger.write(tag + "  " + msg + '\n');
            logger.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the time of the log
     * 
     * @return String
     */
    public String getTime() {
        return dateFormatLog.format(new Date());
    }
    
    /**
     * Set output writer
     * 
     * @param type
     */
    public void setWriter(String type) {
        try {
            logger = null;
            logger = new BufferedWriter(openFile(type));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * If filename is null use standard output for log
     * 
     * @param filename output filename
     * @return
     * @throws IOException 
     */
    private static Writer openFile(String type) throws IOException {
        switch (type) {
            case STANDARD_OUT:
                return new OutputStreamWriter(System.out);
            
            case STANDARD_ERR_OUT:
                return new OutputStreamWriter(System.err);
                
            case FILE_OUT: {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                File file = new File("/var/log/sinapsi/web_service." + dateFormat.format(date) + ".log");
                file.createNewFile();
                file.setWritable(true, false);
                file.setReadable(true, false);
                return new PrintWriter(new FileOutputStream(file, true));          
            }
            
            case WEBSOCKET_FILE_OUT: {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                File file = new File("/var/log/sinapsi/web_socket." + dateFormat.format(date) + ".log");
                file.createNewFile();
                file.setWritable(true, false);
                file.setReadable(true, false);
                return new PrintWriter(new FileOutputStream(file, true));          
            }
                
            case SERVLET_CONTEXT_FILE_OUT: {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                File file = new File("/var/log/sinapsi/servlet_context." + dateFormat.format(date) + ".log");
                file.createNewFile();
                file.setWritable(true, false);
                file.setReadable(true, false);
                return new PrintWriter(new FileOutputStream(file, true));  
            }
            
            case ACTION_LOG_FILE: {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                File file = new File("/var/log/sinapsi/action_log." + dateFormat.format(date) + ".log");
                file.createNewFile();
                file.setWritable(true, false);
                file.setReadable(true, false);
                return new PrintWriter(new FileOutputStream(file, true)); 
            }
                
            default:
                return new OutputStreamWriter(System.out);        
        } 
    }
}
