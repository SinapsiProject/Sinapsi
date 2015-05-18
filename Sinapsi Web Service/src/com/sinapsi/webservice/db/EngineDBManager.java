package com.sinapsi.webservice.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;

import com.sinapsi.engine.Action;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.Trigger;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.webservice.engine.WebServiceEngine;

/**
 * Class that perform engine(triggers, actions, macros) query
 *
 */
public class EngineDBManager {
    private DatabaseController db;
    private HttpServlet http;
       
    /**
     * Default ctor
     */
    public EngineDBManager() {
        db = new DatabaseController();
    }
    
    /**
     * Secondaty ctor
     * @param db database controller
     */
    public EngineDBManager(DatabaseController db) {
        this.db = db;
    }
    
    /**
     * Secondary ctor, use the context listener to access to the db controller
     * @param http http servlet
     */
    public EngineDBManager(HttpServlet http) {
        this.http = http;
        db = (DatabaseController) http.getServletContext().getAttribute("db");
    }

    /**
     * Return the available Actions offered by a specific device
     * 
     * @param idDevice id device
     * @return list of actions
     * @throws SQLException
     */
    public List<MacroComponent> getAvailableAction(int idDevice) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        List<MacroComponent> actions = new ArrayList<MacroComponent>();

        try {
            c = db.connect();
            String query = "SELECT * FROM action, availableaction WHERE action.id = availableaction.idaction AND iddevice = ?";
            s = c.prepareStatement(query);
            s.setInt(1, idDevice);
            r = s.executeQuery();

            while (r.next()) {
                int minVersion = r.getInt("minversion");
                String name = r.getString("name");
                MacroComponent action = db.factory.newActionAbstraction(minVersion, name);
                actions.add(action);
            }

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return actions;
    }

    /**
     * Return the available Trigegrs offered by a specific device
     * 
     * @param idDevice id of the device
     * @return list of trigger
     * @throws SQLException
     */
    public List<MacroComponent> getAvailableTrigger(int idDevice) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        List<MacroComponent> triggers = new ArrayList<MacroComponent>();

        try {
            c = db.connect();
            String query = "SELECT * FROM trigger, availabletrigger WHERE trigger.id = availabletrigger.idtrigger AND iddevice = ?";
            s = c.prepareStatement(query);
            s.setInt(1, idDevice);
            r = s.executeQuery();

            while (r.next()) {
                int minVersion = r.getInt("minversion");
                String name = r.getString("name");
                MacroComponent trigger = db.factory.newTriggerAbstraction(minVersion, name);
                triggers.add(trigger);
            }

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return triggers;
    }
    
    /**
     * Return the list of actions related to a specific macro
     * @param idMacro id of the macro
     * @return list of actions
     * @throws SQLException
     */
    public List<Action> getActions(int idMacro) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        List<Action> actions = new ArrayList<Action>();
        // get the engine from the contex listener
        WebServiceEngine engine = (WebServiceEngine) http.getServletContext().getAttribute("engine");
        DeviceDBManager deviceDBManager = (DeviceDBManager) http.getServletContext().getAttribute("devices_db");
        
        
        try {
            c = db.connect();
            String query = "SELECT iduser, iddevice, name, actionjson " +
                           "FROM actionmacrolist, macro " +
                           "WHERE actionmacrolist.idmacro = macro.id and actionmacrolist.idmacro =  ?";
            s = c.prepareStatement(query);
            s.setInt(1, idMacro);
            r = s.executeQuery();
            
            while(r.next()) {
                ComponentFactory componentFactory = engine.getComponentFactoryForUser(r.getInt("iduser"));
                DeviceInterface device = deviceDBManager.getDevice(r.getInt("iddevice"));
                Action action = componentFactory.newAction(r.getString("name"), r.getString("actionjson"), device);
                actions.add(action);
            }
            
        } catch(SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return actions;
    }

    /**
     * Return the list of actions related to a specific macro
     * @param email email of the user
     * @return list of actions
     * @throws SQLException
     */
    public List<Action> getActions(String email) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        List<Action> actions = new ArrayList<Action>();
        // get the engine from the contex listener
        WebServiceEngine engine = (WebServiceEngine) http.getServletContext().getAttribute("engine");
        DeviceDBManager deviceDBManager = (DeviceDBManager) http.getServletContext().getAttribute("devices_db");
        
        
        try {
            c = db.connect();
            String query = "SELECT iduser, actionmacrolist.iddevice, macro.name, actionjson " +
                           "FROM  actionmacrolist, macro, users " +
                           "WHERE users.id = macro.iduser AND " +
                                 "actionmacrolist.idmacro = macro.id AND " +
                                 "users.email =  ?";
            
            s = c.prepareStatement(query);
            s.setString(1, email);
            r = s.executeQuery();
            
            while(r.next()) {
                ComponentFactory componentFactory = engine.getComponentFactoryForUser(r.getInt("iduser"));
                DeviceInterface device = deviceDBManager.getDevice(r.getInt("iddevice"));
                Action action = componentFactory.newAction(r.getString("name"), r.getString("actionjson"), device);
                actions.add(action);
            }
            
        } catch(SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return actions;
    }
    
    /**
     * Get action, if doesn't exist, create it
     * 
     * @param name name of the action
     * @param versionAction min version of the action
     * @return id of the action
     * @throws SQLException
     */
    public int getIdAction(String name, int versionAction) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        int id = 0;

        try {
            c = db.connect();
            s = c.prepareStatement("SELECT id FROM action WHERE low(name) = low(?)");
            s.setString(1, name);
            r = s.executeQuery();

            if (r.next())
                id = r.getInt("id");

            else {
                s = c.prepareStatement("INSERT INTO action(name, minversion) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                s.setString(1, name);
                s.setInt(2, versionAction);
                s.execute();
                r = s.getGeneratedKeys();
                r.next();
                id = r.getInt("id");
            }

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return id;
    }

    /**
     * Get trigger, if doesn't exist, create it
     * 
     * @param name name of the trigger
     * @param minVersion min version of the trigger
     * @return id of the trigger
     * @throws SQLException
     */
    public int getTrigger(String name, int minVersion) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        int id = 0;

        try {
            c = db.connect();
            s = c.prepareStatement("SELECT id FROM trigger WHERE low(name) = low(?)");
            s.setString(1, name);
            r = s.executeQuery();

            if (r.next())
                id = r.getInt("id");

            else {
                s = c.prepareStatement("INSERT INTO trigger(name, minversion) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                s.setString(1, name);
                s.setInt(2, minVersion);
                s.execute();
                r = s.getGeneratedKeys();
                r.next();
                id = r.getInt("id");
            }

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return id;
    }

    /**
     * Return the name of the trigger from the id
     * @param id id of the trigger
     * @return
     * @throws SQLException 
     */
    public String getTrigger(int id) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        String name = null;

        try {
            c = db.connect();
            s = c.prepareStatement("SELECT name FROM trigger WHERE id = ?");
            s.setInt(1, id);
            r = s.executeQuery();

            if (r.next())
                name = r.getString("name");
            

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return name;
        
    }
    
    /**
     * Add list of available triggers in the db
     * 
     * @param idDevice device id
     * @param triggers list of triggers
     * @throws SQLException
     */
    public void addAvailableTriggers(int idDevice, List<MacroComponent> triggers) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            c = db.connect();

            for (int i = 0; i < triggers.size(); ++i) {
                s = null;
                r = null;
                MacroComponent trigger = triggers.get(i);
                String nameDevice = trigger.getName();
                int versionTrigger = trigger.getMinVersion();
                int idTrigger = getTrigger(nameDevice, versionTrigger);

                String query = "INSERT INTO availabletrigger(idtrigger, iddevice) VALUES(?, ?)";
                s = c.prepareStatement(query);
                s.setInt(1, idTrigger);
                s.setInt(2, idDevice);
                r = s.executeQuery();
            }

        } catch (SQLException e) {
            db.disconnect(c, s, r);
            throw e;
        }
        db.disconnect(c, s);
    }

    /**
     * Add list of available actions in the db
     * 
     * @param idDevice device id
     * @param actions  list of actions
     * @throws SQLException
     */
    public void addAvailableActions(int idDevice, List<MacroComponent> actions) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;

        try {
            c = db.connect();

            for (int i = 0; i < actions.size(); ++i) {
                s = null;
                r = null;
                MacroComponent action = actions.get(i);
                String nameDevice = action.getName();
                int versionTrigger = action.getMinVersion();
                int idAction = getIdAction(nameDevice, versionTrigger);

                String query = "INSERT INTO availableaction(idaction, iddevice) VALUES(?, ?)";
                s = c.prepareStatement(query);
                s.setInt(1, idAction);
                s.setInt(2, idDevice);
                r = s.executeQuery();
            }

        } catch (SQLException e) {
            db.disconnect(c, s, r);
            throw e;
        }
        db.disconnect(c, s);
    }

    /**
     * Return all macro of the user id
     * @param id id of the user
     * @return
     * @throws SQLException 
     */
    public List<MacroInterface> getUserMacro(int id) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        List<MacroInterface> macros = new ArrayList<MacroInterface>();
        DeviceDBManager deviceDb = new DeviceDBManager();

        try {
            c = db.connect();
            s = c.prepareStatement("SELECT * FROM macro WHERE macro.iduser = ?");
            s.setInt(1, id);
            r = s.executeQuery();

            while (r.next()) {
                // create a new macro from the information saved in the db
                MacroInterface macro = db.factory.newMacro(r.getString("name"), r.getInt("id"));
                
                // get the engine from the contex listener
                WebServiceEngine engine = (WebServiceEngine) http.getServletContext().getAttribute("engine");
                // get the component factory of the user
                ComponentFactory componentFactory = engine.getComponentFactoryForUser(id);
                // create a trigger from the information saved in the db
                Trigger trigger = componentFactory.newTrigger(getTrigger(r.getInt("idtrigger")), 
                                                              r.getString("triggerjson"), 
                                                              macro, 
                                                              deviceDb.getDevice(r.getInt("iddevice")));
                // set the trigger
                macro.setTrigger(trigger);
                
                // create a action/actions (of macro:id) from the information saved in the db
                for(Action actionI :  getActions(r.getInt("id"))) 
                    macro.addAction(actionI);                  
                
                macros.add(macro);
            }

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return macros;
    }
    
    /**
     * Delete from the db a macro
     * @param idMacro id of the macro
     * @throws SQLException
     */
    public void deleteUserMacro(int idMacro) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        
        try {
            c = db.connect();
                      
            s = c.prepareStatement("DELETE FROM actionmacrolist WHERE idmacro = ?");       
            s.setInt(1, idMacro);
            s.execute();
            
            s = null;
            
            s = c.prepareStatement("DELETE FROM macro WHERE id = ?");
            s.setInt(1, idMacro);
            s.execute();

        } catch (SQLException e) {
            db.disconnect(c, s);
            throw e;
        }
        db.disconnect(c, s);
    }
    
    /**
     * Add to the db a list of macro
     * @param idUser di of the user
     * @param macros list of macro
     * @throws SQLException
     */
    public void addUserMacros(int idUser, List<MacroInterface> macros) throws SQLException {
        
        for (MacroInterface macro : macros) {
            addUserMacro(idUser, macro);
        }
    }
    
    /**
     * Add to the db a macro, if already exist, updated
     * @param idUser id of the user
     * @param macro macro interface
     * @throws SQLException
     */
    public void addUserMacro(int idUser, MacroInterface macro) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        
        ArrayList<Integer> idMacros = new ArrayList<Integer>();
        ArrayList<Integer> idDevices = new ArrayList<Integer>();
        ArrayList<Integer> minVersionActions = new ArrayList<Integer>();
        ArrayList<String> nameActions = new ArrayList<String>();
        ArrayList<String> parameterActions = new ArrayList<String>();
        
        try {
            c = db.connect();
            
            String nameMacro = macro.getName();
            List<Action> actions = macro.getActions();
            String paramenterTrigger = macro.getTrigger().getActualParameters();
            int idTrigger = getTrigger(macro.getTrigger().getName(), macro.getTrigger().getMinVersion());   
            
            for(Action action : actions) {
                s = null;
                r = null;
                String query = "INSERT INTO macro(name, iduser, triggerjson, iddevice, idtrigger)" +
                                "VALUES(?, ?, ?, ?, ?)";
            
                s = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                s.setString(1, nameMacro);
                s.setInt(2, idUser);
                s.setString(3, paramenterTrigger);
                s.setInt(4, action.getExecutionDevice().getId());
                s.setInt(5, idTrigger);
                s.execute();
                r = s.getGeneratedKeys();
                r.next();
                
                idMacros.add(r.getInt(1));
                nameActions.add(action.getName());
                minVersionActions.add(action.getMinVersion());
                idDevices.add(action.getExecutionDevice().getId());
                parameterActions.add(action.getActualParameters());
            }
            
            s = null;
            r = null;
            String query = "INSERT INTO actionmacrolist(idmacro, idaction, actionjson, iddevice)" +
                            "VALUES(?, ?, ?, ?)";
            
            for(int index = 0; index < idMacros.size(); ++index) {
                s = null;
                r = null;
                s = c.prepareStatement(query);
                s.setInt(1, idMacros.get(index));
                s.setInt(2, getIdAction(nameActions.get(index), minVersionActions.get(index)));
                s.setString(3, parameterActions.get(index));
                s.setInt(4, idDevices.get(index));
                s.execute();   
            } 
                       
        } catch(SQLException ex) {
            db.disconnect(c, s, r);
        }
        
        db.disconnect(c, s, r);
    }
    
}
