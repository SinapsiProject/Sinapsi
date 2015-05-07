package com.sinapsi.webservice.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.sinapsi.model.UserInterface;

/**
 * Class that perform user query
 * @author Ayoub
 *
 */
public class UserManager {
	DatabaseController db;
	
	/**
	 * Default ctor
	 */
	public UserManager() {
		db = new DatabaseController();
	}
	
	 /**
     * Insert new user in the db
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
     * Check if exist a user in the db with email and password
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
}
