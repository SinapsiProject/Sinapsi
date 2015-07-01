package com.sinapsi.webservice.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;

import com.sinapsi.model.UserInterface;
import com.sinapsi.webservice.security.Password;

/**
 * Class that perform user query
 * @author Ayoub
 *
 */
public class UserDBManager {
	DatabaseController db;
	
	/**
	 * Default ctor
	 */
	public UserDBManager() {
		db = new DatabaseController();
	}
	
	/**
	 * Secondaty ctor
	 * 
	 * @param db database controller
	 */
	public UserDBManager(DatabaseController db) {
	    this.db = db;
	}
	
	/**
	 * Secondary ctor, for accessing to the servlet context
	 * 
	 * @param http http servlet, needed to access to the servlet context
	 */
	public UserDBManager(HttpServlet http) {
	    db = (DatabaseController) http.getServletContext().getAttribute("db");   
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
            c = db.connect();
            s = c.prepareStatement("INSERT INTO users(email, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            s.setString(1, email);
            s.setString(2, Password.getSaltedHash(password));
            s.execute();
            r = s.getGeneratedKeys();
            r.next();

            int id = r.getInt(1);
            user = db.factory.newUser(id, email, password);

        } catch (SQLException e) {
            db.disconnect(c, s, r);
            throw e;
        }
        db.disconnect(c, s);
        return user;
    }
    
    /**
     * Update a user token in the db
     * 
     * @param email email of the user
     * @param token secure random token
     * @throws SQLException
     */
    public void updateToken(String email, String token) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
           
        try {
            c = db.connect();
            s = c.prepareStatement("UPDATE users SET token = ? WHERE email = ?");
            s.setString(1, token);
            s.setString(2, email);
            s.execute();
               
        } catch(Exception e) {
            db.disconnect(c, s);
            throw e;
        }
        db.disconnect(c, s);
    }
    
    /**
     * Return all users
     * 
     * @return list of users
     * @throws SQLException
     */
    public List<UserInterface> getUsers() throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        List<UserInterface> users = new ArrayList<UserInterface>();
        DeviceDBManager devicedb = new DeviceDBManager();
        try {
            c = db.connect();
            String query = "SELECT id, email, password FROM users";
            s = c.prepareStatement(query);
            r = s.executeQuery();

            while (r.next()) {
                int id = r.getInt("id");
                String email = r.getString("email");
                String pwd = r.getString("password");
                UserInterface user = db.factory.newUser(id, email, pwd, devicedb.getUserDevices(email));
                users.add(user);
            }

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return users;
    }
    
    /**
     * Check if exist user a user with the token passed 
     * 
     * @param email
     * @param token
     * @return boolean
     * @throws SQLException
     */
    public boolean checkToken(String email, String token) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        boolean tokenMatch = false;
        try {
            c = db.connect();
            s = c.prepareStatement("SELECT id FROM users WHERE email = ? and token = ?");
            s.setString(1, email);
            s.setString(2, token);
            r = s.executeQuery();
            if (r.next()) 
                tokenMatch = true;
            
        } catch(SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return tokenMatch;
    }
    
    /**
     * Check if exist a user in the db with email and password
     * 
     * @param email email to check
     * @param passowrd password to check
     * @throws Exception
     */
    public boolean checkUser(String email, String password) throws Exception {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        boolean passwordMatch = false;
        try {
            c = db.connect();
            s = c.prepareStatement("SELECT * FROM users WHERE email = ?");
            s.setString(1, email);
            r = s.executeQuery();
            if (r.next()) {
                // true if the password match the hash of the stored password,
                // false otherwise
                passwordMatch = Password.check(password, r.getString("password"));
            }
        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return passwordMatch;
    }
    
    /**
     * Return user by id
     * 
     * @param id id of the user
     * @return user user to return
     * @throws SQLException
     */
    public UserInterface getUserById(int id) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        UserInterface user = null;

        try {
            c = db.connect();
            s = c.prepareStatement("SELECT * FROM users WHERE id = ?");
            s.setInt(1, id);
            r = s.executeQuery();
            if (r.next())
                user = db.factory.newUser(id, r.getString("email"), r.getString("password"));

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return user;
    }
    

    /**
     * Return user by email
     * 
     * @param email email of the user 
     * @return user to return
     * @throws SQLException
     */
    public UserInterface getUserByEmail(String email) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        UserInterface user = null;

        try {
            c = db.connect();
            s = c.prepareStatement("SELECT * FROM users WHERE email = ?");
            s.setString(1, email);
            r = s.executeQuery();
            if (r.next())
                user = db.factory.newUser(r.getInt("id"), email, r.getString("password"));

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return user;
    }
    
    /**
     * Given a device id return the email of user 
     * @param idDevice id of the device
     * @return email of the user
     * @throws SQLException
     */
    public String getUserEmail(int idDevice) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        String email = null;

        try {
            c = db.connect();
            s = c.prepareStatement("SELECT email FROM users, device WHERE users.id = device.iduser AND device.id = ?");
            s.setInt(1, idDevice);
            r = s.executeQuery();
            if (r.next())
                email = r.getString("email");

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return email;
    }
}
