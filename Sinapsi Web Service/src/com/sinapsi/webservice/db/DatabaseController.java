package com.sinapsi.webservice.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import com.sinapsi.model.*;
import com.sinapsi.model.impl.FactoryModel;

/**
 * This class gives a database interface, performing connection and disconnection of the db.
 *
 */
public class DatabaseController {
    FactoryModelInterface factory;
    private String url;
    private String driver;
    private String user;
    private String password;

    /**
     * Class constructor
     */
    public DatabaseController() {
        factory = new FactoryModel();
        ResourceBundle bundle = ResourceBundle.getBundle("configuration");
        url = bundle.getString("database.url");
        driver = bundle.getString("database.driver");
        user = bundle.getString("database.user");
        password = bundle.getString("database.password");
        
        try {
            Class.forName(driver);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Establish connection to the db
     * 
     * @return Connection object
     * @throws SQLException
     */
    Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Disconnect from the db
     * 
     * @param c Connection object
     * @param s Statement object
     */
    void disconnect(Connection c, Statement s) {
        disconnect(c, s, null);
    }

    /**
     * Disconnect from the db
     * 
     * @param c Connection object
     * @param s Statement object
     * @param r Resultset object
     */
    void disconnect(Connection c, Statement s, ResultSet r) {
        try {
            r.close();
        } catch (Throwable t) {
        }

        try {
            s.close();
        } catch (Throwable t) {
        }

        try {
            c.close();
        } catch (Throwable t) {
        }
    }
}
