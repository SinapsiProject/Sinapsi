package com.sinapsi.webservice.db;
//balbal
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import com.sinapsi.model.FactoryModel;
import com.sinapsi.model.UserInterface;

/**
 * This class gives a database interface to the clients, performing queries and
 * returning data from the database
 *
 */
public class DatabaseManager {
    private FactoryModel factory;
    private String url;
    private String driver;

    /**
     * Class constructor
     */
    public DatabaseManager() {
        //TODO create concrete model factory
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
     * @throws Exception 
     */
    private UserInterface registerUser(String email, String password) throws Exception {
        Connection c = null; 
        PreparedStatement s = null;
        ResultSet r = null;
        UserInterface user = null;
        try {
            c = connect();
            s = c.prepareStatement("INSERT INTO users(email, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            s.setString(1, email);
            s.setString(2, Password.getSaltedHash(password));
            s.execute();
            r = s.getGeneratedKeys();
            r.next();
            
            int id = r.getInt(1);
            user = factory.newUser(id, email, email);
            
        } catch(SQLException e) {
            disconnect(c, s, r);
            throw e;
        }
        disconnect(c, s);
        return user;
    }

    /**
     * Login the user to the system
     * 
     * @param email
     * @param passowrd
     * @throws Exception 
     */
    private boolean loginUser(String email, String password) throws Exception {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        boolean passwordMatch = false;
        try {
            c = connect();
            s = c.prepareStatement("SELECT * FROM users WHERE email = ?");
            s.setString(1, email);
            r = s.executeQuery();
            if(r.next()) {
                // true if the password match the hash of the stored password, false otherwise
                passwordMatch =  Password.check(password, r.getString("password"));
            }
        }catch(SQLException ex) {
            disconnect(c, s, r);
            throw ex;
        }
        disconnect(c, s, r);
        return passwordMatch;
    }
}
