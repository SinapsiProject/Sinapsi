package com.sinapsi.webservice.system;

import java.sql.SQLException;

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

/**
 * Context Listener class.
 * Init the database controller and the Web service Engine
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
   
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        
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
        
        // init the engine
        try {
            engine.initEngines(userDbManager.getUsers());
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        // add object to the contex 
        context.setAttribute("db", db);
        context.setAttribute("engine", engine); 
        context.setAttribute("users_db", userDbManager);
        context.setAttribute("keys_db", keysDbManager);
        context.setAttribute("engines_db", engineDbManager);
        context.setAttribute("devices_db", deviceDbManager);   
    }
}
