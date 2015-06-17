package com.sinapsi.webservice.system;

import java.io.IOException;
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
import com.sinapsi.webservice.websocket.Server;

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
    private Server wsServer;
   
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {

        
        // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println(String.format("deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
                System.out.println(String.format("Error deregistering driver %s", driver));
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
        
        // initialize web socket server on port 8887
        try {
            wsServer = new Server(8887);
        } catch (UnknownHostException e2) {
            e2.printStackTrace();
        }
        
        // start web socket server
        new Thread() {
            public void run() {
                try {
                    wsServer.init();
                } catch (InterruptedException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();   
        
        // initialize Sinapsi engine
        try {
            engine.addWSServer(wsServer);
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
        context.setAttribute("wsserver", wsServer);         
    }
}
