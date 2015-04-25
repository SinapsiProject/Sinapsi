package com.sinapsi.webservice.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.List;

import com.sinapsi.model.*;
import com.sinapsi.model.impl.FactoryModel;

/**
 * This class gives a database interface to the clients, performing queries and
 * returning data from the database
 *
 */
public class DatabaseManager {
    private FactoryModelInterface factory;
    private String url;
    private String driver;

    /**
     * Class constructor
     */
    public DatabaseManager() {
        //TODO create concrete model factory
    	factory = new FactoryModel();
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
        return DriverManager.getConnection(url, "andrej", "ragnarock");
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
     * Insert new user in the db
     * 
     * @param email email of the new user
     * @param password password of the news user
     * @throws Exception 
     */
    public UserInterface newUser(String email, String password) throws Exception {
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
            user = factory.newUser(id, email, password);
            
        } catch(SQLException e) {
            disconnect(c, s, r);
            throw e;
        }
        disconnect(c, s);
        return user;
    }

    /**
     * Check if exist a user in the db with email and password 
     * 
     * @param email
     * @param passowrd
     * @throws Exception 
     */
    public boolean checkUser(String email, String password) throws Exception {
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
    
    /**
     * Return user by id
     * 
     * @param id id of the user
     * @return user
     * @throws SQLException 
     */
    public UserInterface getUserById(int id) throws SQLException {
    	 Connection c = null;
         PreparedStatement s = null;
         ResultSet r = null;
         UserInterface user = null;
         
         try {
             c = connect();
             s = c.prepareStatement("SELECT * FROM users WHERE id = ?");
             s.setInt(1, id);
             r = s.executeQuery();
             if(r.next()) 
                 user = factory.newUser(id, r.getString("email"), r.getString("password"));
             
         }catch(SQLException ex) {
             disconnect(c, s, r);
             throw ex;
         }
         disconnect(c, s, r);
         return user;
    }
    
    /**
     * Return user by email
     * 
     * @param email
     * @return user
     * @throws SQLException
     */
    public UserInterface getUserByEmail(String email) throws SQLException {
   	 Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        UserInterface user = null;
        
        try {
            c = connect();
            s = c.prepareStatement("SELECT * FROM users WHERE email = ?");
            s.setString(1, email);
            r = s.executeQuery();
            if(r.next()) 
                user = factory.newUser(r.getInt("id"), email, r.getString("password"));
            
        }catch(SQLException ex) {
            disconnect(c, s, r);
            throw ex;
        }
        disconnect(c, s, r);
        return user;
   }
    
    /**
     * Return all device linked to user email
     * 
     * @param email email of user
     * @return list of devices
     * @throws SQLException 
     */
    public List<DeviceInterface> getUserDevices(String email) throws SQLException {
    	UserInterface user =  getUserByEmail(email);
    	Connection c = null;
    	PreparedStatement s = null;
    	ResultSet r = null;
    	List<DeviceInterface> devices = new ArrayList<DeviceInterface>();
    	
    	try {
    		c = connect();
    		String query ="SELECT * FROM device WHERE iduser = (SELECT id FROM users WHERE email = ?)";
    		s = c.prepareStatement(query);
    		s.setString(1, email);
    		r = s.executeQuery();
    		
    		while(r.next()) {
    			int id = r.getInt("id");
    			String name = r.getString("name");
    			String model = r.getString("model");
    			String type = r.getString("type");
    			int version = r.getInt("version");
    			DeviceInterface device = factory.newDevice(id, name, model, type, user, version);
    			devices.add(device);
    		}
    		
    	} catch(SQLException ex) {
    		disconnect(c, s, r);
    		throw ex;
    	}
    	disconnect(c, s, r);
    	return devices;
    }

    /**
     * Return the available Actions offered by a specific device
     * 
     * @param idDevice id device
     * @return list of actions
     * @throws SQLException 
     */
     public List<Action> getAvailableAction(int idDevice) throws SQLException {
    	Connection c = null;
    	PreparedStatement s = null;
    	ResultSet r = null;
    	List<Action> actions = new ArrayList<Action>();
    	
    	try {
    		c = connect();
    		String query = "SELECT * FROM action WHERE id = (SELECT * FROM availableaction WHERE iddevice = ?)";
    		s = c.prepareStatement(query);
    		s.setInt(1, idDevice);
    		r = s.executeQuery();
    		
    		while(r.next()) {
    			int id = r.getInt("id");
    			int minVersion = r.getInt("minversion");
    			String name = r.getString("name");
    			
    		}
    		
    	} catch(SQLException ex) {
    		disconnect(c, s, r);
    		throw ex;
    	}
    	disconnect(c, s, r);
    	return null;
    }
}
