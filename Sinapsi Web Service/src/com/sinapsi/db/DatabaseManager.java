package com.sinapsi.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * This class gives a database interface to the clients, performing queries and
 * returning data from the database
 * 
 * @author Ayoub Ouarrak
 *
 */
public class DatabaseManager {
    private String url;
    private String driver;

    /**
     * Class constructor
     */
    public DatabaseManager() {
        ResourceBundle bundle = ResourceBundle.getBundle("configuration");
        url = bundle.getString("database.url");
        driver = bundle.getString("database.driver");

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
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * Disconnect from the db
     * 
     * @param c Connection object
     * @param s Statement object
     */
    private void disconnect(Connection c, Statement s) {
        disconnect(c, s, null);
    }

    /**
     * Disconnect from the db
     * 
     * @param c Connection object
     * @param s Statement object
     * @param r Resultset object
     */
    private void disconnect(Connection c, Statement s, ResultSet r) {
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

    /**
     * Register a new user to the system
     * 
     * @param email
     * @param password
     */
    private void registerUser(String email, String password) {

    }

    /**
     * Login the user to the system
     * 
     * @param email
     * @param passowrd
     */
    private void loginUser(String email, String passowrd) {

    }
}
