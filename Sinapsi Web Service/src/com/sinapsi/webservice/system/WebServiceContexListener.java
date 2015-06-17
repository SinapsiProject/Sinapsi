package com.sinapsi.webservice.system;

import java.net.UnknownHostException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.sinapsi.webservice.db.DatabaseController;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.EngineDBManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.db.UserDBManager;
import com.sinapsi.webservice.engine.WebServiceEngine;
import com.sinapsi.webservice.engine.WebServiceLog;
import com.sinapsi.webservice.websocket.WSServerThread;

/**
 * Context Listener class.
 * Initialize the database controller, Web service Engine and the web socket server
 *
 */
@WebListener
public class WebServiceContexListener implements ServletContextListener {
    private ServletContext context;
    private DatabaseController db;
    private WebServiceEngine engine;
    private UserDBManager userDbManager;
    private KeysDBManager keysDbManager;
    private EngineDBManager engineDbManager;
    private DeviceDBManager deviceDbManager;
    private Thread wsserverThread = null;
    private WSServerThread wsserverRunnable = null;
    private WebServiceLog sclog = new WebServiceLog(WebServiceLog.SERVLET_CONTEXT_FILE_OUT);
   
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // stop the web socket thread
        try {
            sclog.log(sclog.getTime(), "Stopping web socket thread");
            if (wsserverThread != null) {
                wsserverRunnable.terminate();
                wsserverThread.join();
               
                sclog.log(sclog.getTime(), "Thread successfully stopped.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                sclog.log(sclog.getTime(),"Deregistering jdbc driver");
            } catch (SQLException e) {
                sclog.log(sclog.getTime(),"Error deregistering driver");
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent e) {
        context = e.getServletContext();
        
        // create objects
        db = new DatabaseController();
        engine = new WebServiceEngine();
        userDbManager = new UserDBManager(db);
        keysDbManager = new KeysDBManager(db);
        engineDbManager = new EngineDBManager(db);
        deviceDbManager = new DeviceDBManager(db);
                
        // start  web socket server thread
        try {
            wsserverRunnable = new WSServerThread(8887);
            wsserverThread = new Thread(wsserverRunnable);
            
            sclog.log(sclog.getTime(), "Starting websocket server thread");
            wsserverThread.start();
            
            sclog.log(sclog.getTime(), "Background process successfully started.");
            
        } catch (UnknownHostException e2) {
            e2.printStackTrace();
        }
       
        
        // initialize Sinapsi engine
        try {
            engine.addWSServer(wsserverRunnable.getServer());
            engine.initEngines(userDbManager.getUsers());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        
        // add object to the contex 
        context.setAttribute("db", db);
        context.setAttribute("engine", engine); 
        context.setAttribute("users_db", userDbManager);
        context.setAttribute("keys_db", keysDbManager);
        context.setAttribute("engines_db", engineDbManager);
        context.setAttribute("devices_db", deviceDbManager);  
        context.setAttribute("wsserver", wsserverRunnable.getServer());         
    }
}
