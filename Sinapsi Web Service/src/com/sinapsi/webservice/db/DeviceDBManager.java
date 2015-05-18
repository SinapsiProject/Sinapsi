package com.sinapsi.webservice.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServlet;

import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.UserInterface;

/**
 * Class that perform devices query 
 *
 */
public class DeviceDBManager extends UserDBManager {
	private DatabaseController db;
	
	/**
	 * Default ctor
	 */
	public DeviceDBManager() {
		db = new DatabaseController();
	}
	
	/**
     * Secondaty ctor
     * @param db database controller
     */
	public DeviceDBManager(DatabaseController db) {
	    this.db = db;
	}
	
	 /**
     * Secondary ctor, use the context listener to access to the db controller
     * @param http http servlet
     */
	public DeviceDBManager(HttpServlet http) {
	    db = (DatabaseController) http.getServletContext().getAttribute("db");
	}
	
    /**
     * Check if the device with name, model associate to idUser exist
     * @param name name of the device
     * @param model model of the device
     * @param idUser id of the user
     * @return boolean
     * @throws SQLException 
     */
    public boolean checkDevice(String name, String model, int idUser) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        boolean deviceExist = false;
        
        try {
            c = db.connect();
            String query = "SELECT * FROM device WHERE lower(name) = lower(?) AND lower(model) = lower(?) and iduser = ?";
            s = c.prepareStatement(query);
            s.setString(1, name);
            s.setString(2, model);
            s.setInt(3, idUser);
            r = s.executeQuery();
            if (r.next()) {
                deviceExist = true;
            }
        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return deviceExist;
    }
    
    /**
     * Insert new device in the db
     * @param name name of the device
     * @param model model of the device
     * @param type type of the device (mobile/desktop)
     * @param idUser id of the user
     * @param clientVersion version of the client
     * @return device interface
     * @throws Exception
     */
    public DeviceInterface newDevice(String name, String model, String type, int idUser, int clientVersion) throws Exception {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        UserInterface user = getUserById(idUser);
        DeviceInterface device = null;
        
        try {
            c = db.connect();
            String query = "INSERT INTO device(name, model, type, iduser, version) VALUES (?, ?, ?, ?, ?)";
            s = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            s.setString(1, name);
            s.setString(2, model);
            s.setString(3, type);
            s.setInt(4, idUser);
            s.setInt(5, clientVersion);
            s.execute();
            r = s.getGeneratedKeys();
            r.next();

            int id = r.getInt(1);
            device = db.factory.newDevice(id, name, model, type, user, clientVersion);

        } catch (SQLException e) {
            db.disconnect(c, s, r);
            throw e;
        }
        db.disconnect(c, s);
        return device;
    }
    
    /**
     * Return the device with name, model of the id user
     * @param name name of the device
     * @param model model of the device
     * @param idUser id of the user
     * @return device interface
     * @throws SQLException
     */
    public DeviceInterface getDevice(String name, String model, int idUser) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        DeviceInterface device = null;

        try {
            c = db.connect();
            String query = "SELECT * FROM device WHERE lower(name) = lower(?) AND lower(model) = lower(?) and iduser = ?";
            s = c.prepareStatement(query);
            s.setString(1, name);
            s.setString(2, model);
            s.setInt(3, idUser);
            r = s.executeQuery();
            if (r.next())
                device = db.factory.newDevice(r.getInt("id"), name, model, r.getString("type"), getUserById(idUser), r.getInt("version"));

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return device;
    }
    
    /**
     * Return the device with id
     * @param idUser id of the user
     * @return device interface
     * @throws SQLException
     */
    public DeviceInterface getDevice(int idDevice) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        DeviceInterface device = null;

        try {
            c = db.connect();
            String query = "SELECT * FROM device WHERE idu = ?";
            s = c.prepareStatement(query);
            s.setInt(1, idDevice);
            r = s.executeQuery();
            if (r.next()) {
                device = db.factory.newDevice(idDevice, 
                                              r.getString("name"), 
                                              r.getString("model"), 
                                              r.getString("type"), 
                                              getUserById(r.getInt("iduser")), 
                                              r.getInt("version"));
            }

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return device;
    }
    
    /**
     * Return the name and the model of the device with id
     * @param idDevice id of the device
     * @return
     * @throws SQLException
     */
    public HashMap.SimpleEntry<String, String> getInfoDevice(int idDevice) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        HashMap.SimpleEntry<String, String> nameModel = null;

        try {
            c = db.connect();
            String query = "SELECT name, model FROM device WHERE id = ?";
            s = c.prepareStatement(query);
            s.setInt(1, idDevice);

            r = s.executeQuery();
            if (r.next())
                nameModel = new HashMap.SimpleEntry<String, String>(r.getString("name"), r.getString("model"));

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return nameModel;
        
    }
    
    /**
     * Return all device linked to user email
     * @param email email of user
     * @return list of devices
     * @throws SQLException
     */
    public List<DeviceInterface> getUserDevices(String email) throws SQLException {
        UserInterface user = getUserByEmail(email);
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        List<DeviceInterface> devices = new ArrayList<DeviceInterface>();

        try {
            c = db.connect();
            String query = "SELECT * FROM device, users WHERE device.iduser = users.id and email = ?";
            s = c.prepareStatement(query);
            s.setString(1, email);
            r = s.executeQuery();

            while (r.next()) {
                int id = r.getInt("id");
                String name = r.getString("name");
                String model = r.getString("model");
                String type = r.getString("type");
                int version = r.getInt("version");
                DeviceInterface device = db.factory.newDevice(id, name, model, type, user, version);
                devices.add(device);
            }

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return devices;
    }

    /**
     * Return the id of web device of the user identify by the id, if the user hasn't any
     * web device connected, create a new one
     * 
     * @param idUser id user
     * @throws Exception 
     */
    public int getIdWebDevice(int idUser) throws Exception {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        int idDevice = -1;
        
        try {
            c = db.connect();
            String query = "SELECT id FROM device WHERE iduser = ?";
            s = c.prepareStatement(query);
            s.setInt(1, idUser);
            r = s.executeQuery();
            
            if(r.next()) {
                idDevice = r.getInt("id");
            
            // create a new Web Device
            } else {
                DeviceInterface newDevice = newDevice("Cloud", "Sinapsi", "Web", idUser, 1);
                idDevice = newDevice.getId();
            }
        } catch(SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        
        db.disconnect(c, s, r);
        return idDevice;
    }
}
