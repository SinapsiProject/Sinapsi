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
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.TriggerDescriptor;
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
     * Secondary ctor
     * 
     * @param db database controller
     */
    public EngineDBManager(DatabaseController db) {
        this.db = db;
    }
    
    /**
     * Secondary ctor, use the context listener to access to the db controller
     * 
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
    public List<ActionDescriptor> getAvailableActions(int idDevice) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        List<ActionDescriptor> actions = new ArrayList<ActionDescriptor>();

        try {
            c = db.connect();
            String query = "SELECT * FROM action, availableaction WHERE action.id = availableaction.idaction AND iddevice = ?";
            s = c.prepareStatement(query);
            s.setInt(1, idDevice);
            r = s.executeQuery();

            while (r.next()) {
                int minVersion = r.getInt("minversion");
                String name = r.getString("name");
                ActionDescriptor action = db.factory.newActionDescriptor(minVersion, name, r.getString("parameters"));
                actions.add(action);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return actions;
    }

    /**
     * Return the available triggers offered by a specific device
     * 
     * @param idDevice id of the device
     * @return list of trigger
     * @throws SQLException
     */
    public List<TriggerDescriptor> getAvailableTriggers(int idDevice) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        List<TriggerDescriptor> triggers = new ArrayList<TriggerDescriptor>();

        try {
            c = db.connect();
            String query = "SELECT * FROM trigger, availabletrigger WHERE trigger.id = availabletrigger.idtrigger AND iddevice = ?";
            s = c.prepareStatement(query);
            s.setInt(1, idDevice);
            r = s.executeQuery();

            while (r.next()) {
                int minVersion = r.getInt("minversion");
                String name = r.getString("name");
                TriggerDescriptor trigger = db.factory.newTriggerDescriptor(minVersion, name, r.getString("parameters"));
                triggers.add(trigger);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return triggers;
    }
    
    /**
     * Return the list of actions related to a specific macro
     * 
     * @param idMacro id of the macro
     * @return list of actions
     * @throws SQLException
     */
    public List<Action> getActions(int idMacro, ComponentFactory componentFactory) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        List<Action> actions = new ArrayList<Action>();
        DeviceDBManager deviceDBManager = new DeviceDBManager();
        
        
        try {
            c = db.connect();
            String query = "SELECT iduser, actionmacrolist.iddevice, action.name, actionjson " +
                           "FROM actionmacrolist, macro, action " +
                           "WHERE actionmacrolist.idmacro = macro.id AND " +
                                 "action.id = actionmacrolist.idaction AND " +
                                 "actionmacrolist.idmacro =  ?";
            s = c.prepareStatement(query);
            s.setInt(1, idMacro);
            r = s.executeQuery();
            
            while(r.next()) {
                DeviceInterface device = deviceDBManager.getDevice(r.getInt("iddevice"));
                Action action = componentFactory.newAction(r.getString("name"), r.getString("actionjson"), device.getId());
                actions.add(action);
            }
            
        } catch(SQLException ex) {
            ex.printStackTrace();
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return actions;
    }

    /**
     * Return the list of actions related to a specific macro
     * 
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
            String query = "SELECT iduser, actionmacrolist.iddevice, action.name, actionjson " +
                           "FROM actionmacrolist, macro, action " +
                           "WHERE actionmacrolist.idmacro = macro.id AND " +
                                 "action.id = actionmacrolist.idaction AND " +
                                 "actionmacrolist.idmacro =  ?";
            
            s = c.prepareStatement(query);
            s.setString(1, email);
            r = s.executeQuery();
            
            while(r.next()) {
                ComponentFactory componentFactory = engine.getComponentFactoryForUser(r.getInt("iduser"));
                DeviceInterface device = deviceDBManager.getDevice(r.getInt("iddevice"));
                Action action = componentFactory.newAction(r.getString("name"), r.getString("actionjson"), device.getId());
                actions.add(action);
            }
            
        } catch(SQLException ex) {
            ex.printStackTrace();
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
    public int getIdAction(String name, int versionAction, String parameters) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        int id = 0;

        try {
            c = db.connect();
            s = c.prepareStatement("SELECT id FROM action WHERE name = ?");
            s.setString(1, name);
            r = s.executeQuery();

            if (r.next())
                id = r.getInt("id");

            else {
                s = c.prepareStatement("INSERT INTO action(name, minversion, parameters) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                s.setString(1, name);
                s.setInt(2, versionAction);
                s.setString(3, parameters);
                s.execute();
                r = s.getGeneratedKeys();
                r.next();
                id = r.getInt("id");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
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
    public int getTrigger(String name, int minVersion, String parameters) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        int id = 0;

        try {
            c = db.connect();
            s = c.prepareStatement("SELECT id FROM trigger WHERE name = ?");
            s.setString(1, name);
            r = s.executeQuery();

            if (r.next())
                id = r.getInt("id");

            else {
                s = c.prepareStatement("INSERT INTO trigger(name, minversion, parameters) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                s.setString(1, name);
                s.setInt(2, minVersion);
                s.setString(3, parameters);
                s.execute();
                r = s.getGeneratedKeys();
                r.next();
                id = r.getInt("id");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return id;
    }

    /**
     * Return the name of the trigger from the id
     * 
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
            ex.printStackTrace();
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return name;
        
    }
    
    /**
     * Control if there are records in available triggers
     * @param idDevice id device
     * @return boolean
     * @throws SQLException
     */
    public boolean emptyAvailableTriggers(int idDevice) throws SQLException {
       Connection c = null;
       PreparedStatement s = null;
       ResultSet r = null;
       boolean found = true;
       
       try {
           c = db.connect();
           s = c.prepareStatement("SELECT * FROM availabletrigger WHERE iddevice = ?");
           s.setInt(1, idDevice);
           r = s.executeQuery();

           if (r.next())
               found = false;
           

       } catch (SQLException ex) {
           ex.printStackTrace();
           db.disconnect(c, s, r);
           throw ex;
       }
       db.disconnect(c, s, r);
       return found;
    }
    
    /**
     * Control if there are records in available actions
     * @param idDevice id device
     * @return boolean
     * @throws SQLException
     */
    public boolean emptyAvailableActions(int idDevice) throws SQLException {
       Connection c = null;
       PreparedStatement s = null;
       ResultSet r = null;
       boolean found = true;
       
       try {
           c = db.connect();
           s = c.prepareStatement("SELECT * FROM availableaction WHERE iddevice = ?");
           s.setInt(1, idDevice);
           r = s.executeQuery();

           if (r.next())
               found = false;
           

       } catch (SQLException ex) {
           ex.printStackTrace();
           db.disconnect(c, s, r);
           throw ex;
       }
       db.disconnect(c, s, r);
       return found;
    }
    
    /**
     * Add list of available triggers in the db
     * 
     * @param idDevice device id
     * @param triggers list of triggers
     * @throws SQLException
     */
    public void addAvailableTriggers(int idDevice, List<TriggerDescriptor> triggers) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        c = db.connect();
        c.setAutoCommit(false);
        
        // clean the available triggers
        if(!emptyAvailableTriggers(idDevice)) {
           try {
               String query = "DELETE FROM availabletrigger WHERE iddevice = ?";
               s = c.prepareStatement(query);
               s.setInt(1, idDevice);
               s.execute();
               
           } catch (SQLException e) {
               e.printStackTrace();
               c.rollback();
               db.disconnect(c, s, r);
               throw e;
           }
        }
        
        // add available triggers
        try {
            for (int i = 0; i < triggers.size(); ++i) {
                s = null;
                r = null;
                TriggerDescriptor trigger = triggers.get(i);
                String nameDevice = trigger.getName();
                int versionTrigger = trigger.getMinVersion();
                int idTrigger = getTrigger(nameDevice, versionTrigger, trigger.getFormalParameters());

                String query = "INSERT INTO availabletrigger(idtrigger, iddevice) VALUES(?, ?)";
                s = c.prepareStatement(query);
                s.setInt(1, idTrigger);
                s.setInt(2, idDevice);
                s.execute();
            }

        } catch (SQLException e) {
            c.rollback();
            db.disconnect(c, s, r);
            throw e;
        }
        c.commit();
        db.disconnect(c, s);
    }

    /**
     * Add list of available actions in the db
     * 
     * @param idDevice device id
     * @param actions  list of actions
     * @throws SQLException
     */
    public void addAvailableActions(int idDevice, List<ActionDescriptor> actions) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        c = db.connect();
        c.setAutoCommit(false);
        
        // clean the available triggers
        if(!emptyAvailableActions(idDevice)) {
           try {
               String query = "DELETE FROM availableaction WHERE iddevice = ?";
               s = c.prepareStatement(query);
               s.setInt(1, idDevice);
               s.execute();
               
           } catch (SQLException e) {
               e.printStackTrace();
               c.rollback();
               db.disconnect(c, s, r);
               throw e;
           }
        }
        
        // add available actions
        try {
            for (int i = 0; i < actions.size(); ++i) {
                s = null;
                r = null;
                ActionDescriptor action = actions.get(i);
                String nameDevice = action.getName();
                int versionTrigger = action.getMinVersion();
                int idAction = getIdAction(nameDevice, versionTrigger, action.getFormalParameters());

                String query = "INSERT INTO availableaction(idaction, iddevice) VALUES(?, ?)";
                s = c.prepareStatement(query);
                s.setInt(1, idAction);
                s.setInt(2, idDevice);
                s.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            c.rollback();
            db.disconnect(c, s, r);
            throw e;
        }
        c.commit();
        db.disconnect(c, s);
    }

    /**
     * Return macro with id
     * 
     * @param id id of the macro
     * @return
     * @throws SQLException 
     */
    public MacroInterface getMacro(int id) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        MacroInterface macro = null;
        DeviceDBManager deviceDb = new DeviceDBManager();
        
        try {
            c = db.connect();
            s = c.prepareStatement("SELECT * FROM macro WHERE macro.id = ?");
            s.setInt(1, id);
            r = s.executeQuery();

            if(r.next()) {
                // create a new macro from the information saved in the db
                macro = db.factory.newMacro(r.getString("name"), r.getInt("id"));
                
                // get the engine from the contex listener
                WebServiceEngine engine = (WebServiceEngine) http.getServletContext().getAttribute("engine");
                
               
                // get the component factory of the user
                ComponentFactory componentFactory = engine.getComponentFactoryForUser(id);
                // create a trigger from the information saved in the db
                Trigger trigger = componentFactory.newTrigger(getTrigger(r.getInt("idtrigger")), 
                                                              r.getString("triggerjson"), 
                                                              macro, 
                                                              deviceDb.getDevice(r.getInt("iddevice")).getId());
                // set the trigger
                macro.setTrigger(trigger);
                
                // create a action/actions (of macro:id) from the information saved in the db
                for(Action actionI : getActions(r.getInt("id"), componentFactory)) 
                    macro.addAction(actionI);                  
                
                macro.setIconName(r.getString("icon"));
                macro.setValid(r.getInt("incomplete") == 0 ? true : false);
                macro.setMacroColor(r.getString("color"));
                macro.setExecutionFailurePolicy(r.getString("errorpolicy"));
            }

        } catch (SQLException ex) {
            db.disconnect(c, s, r);
            throw ex;
        }
        db.disconnect(c, s, r);
        return macro;
    }
    
    /**
     * Return all macro of the user id
     * 
     * @param id id of the user
     * @return
     * @throws SQLException 
     */
    public List<MacroInterface> getUserMacro(int id, ComponentFactory componentFactory) throws SQLException {
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
                          
                // create a trigger from the information saved in the db
                Trigger trigger = componentFactory.newTrigger(getTrigger(r.getInt("idtrigger")), 
                                                              r.getString("triggerjson"), 
                                                              macro, 
                                                              deviceDb.getDevice(r.getInt("iddevice")).getId());
                // set the trigger
                macro.setTrigger(trigger);
                
                // create a action/actions (of macro:id) from the information saved in the db
                List<Action> actions = getActions(r.getInt("id"), componentFactory);
                for(Action actionI :  actions) 
                    macro.addAction(actionI);                  
                
                macro.setIconName(r.getString("icon"));
                macro.setValid(r.getInt("incomplete") == 0 ? true : false);
                macro.setMacroColor(r.getString("color"));
                macro.setExecutionFailurePolicy(r.getString("errorpolicy"));
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
     * Control if exist a macro by id
     * 
     * @param id id of the macro
     * @return boolean
     * @throws SQLException
     */
    public boolean checkMacro(int id) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        boolean macroFound = false;
        
        try {
            c = db.connect();
            s = c.prepareStatement("SELECT * FROM macro WHERE id = ?");
            s.setInt(1, id);
            r = s.executeQuery();
        
            if(r.next())
                macroFound = true;
            
        } catch(SQLException e) {
            db.disconnect(c, s, r);
            throw e;
        }
        
        db.disconnect(c, s, r);
        return macroFound;
    }
    
    /**
     * Delete from the db a macro
     * 
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
     * 
     * @param idUser di of the user
     * @param macros list of macro
     * @throws SQLException
     */
    public List<Integer> addUserMacros(int idUser, List<MacroInterface> macros) throws SQLException {
        List<Integer> ids = new ArrayList<Integer>();
        
        for (MacroInterface macro : macros) {
            ids.add(addUserMacro(idUser, macro));
        }
        return ids;
    }
    
    /**
     * Add to the db a macro, if already exist, updated
     * 
     * @param idUser id of the user
     * @param macro macro interface
     * @return id of the macro
     * @throws SQLException
     */
    public int addUserMacro(int idUser, MacroInterface macro) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        int idMacro = -1;
        
        // macro already exist
        if(checkMacro(macro.getId())) 
            return updateMacro(idUser, macro);
        
        
        
        try {
            c = db.connect();
            c.setAutoCommit(false);
            List<Action> actions = macro.getActions();
            int idTrigger = getTrigger(macro.getTrigger().getName(), macro.getTrigger().getMinVersion(), macro.getTrigger().getFormalParameters());   
            int idDevice =  macro.getTrigger().getExecutionDevice().getId();
            int counter = 0;
            
            String query = "INSERT INTO macro(name, iduser, triggerjson, iddevice, idtrigger, icon, color, incomplete, errorpolicy)" +
                           "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            s = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            s.setString(1, macro.getName());
            s.setInt(2, idUser);
            s.setString(3, macro.getTrigger().getActualParameters());
            s.setInt(4, idDevice);
            s.setInt(5, idTrigger);
            s.setString(6, macro.getIconName());
            s.setString(7, macro.getMacroColor());
            s.setInt(8, macro.isValid() ? 0 : 1);
            s.setString(9, macro.getExecutionFailurePolicy());
                              
            s.execute();
            r = s.getGeneratedKeys();
            r.next();
            idMacro = r.getInt("id");  
            
            String query2 = "INSERT INTO actionmacrolist(idmacro, idaction, actionjson, iddevice, counter)" +
                            "VALUES(?, ?, ?, ?, ?)";
            
            for(Action action : actions) {
                s = null;
                r = null;
                s = c.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);
                s.setInt(1, idMacro);
                s.setInt(2, getIdAction(action.getName(), action.getMinVersion(), action.getFormalParameters()));
                s.setString(3, action.getActualParameters());
                s.setInt(4, action.getExecutionDevice().getId());
                s.setInt(5, counter++);
                s.execute();   
                r = s.getGeneratedKeys();
                r.next();
            } 
                
            c.commit();
            db.disconnect(c, s, r);
            
        } catch(SQLException ex) {
            ex.printStackTrace();
            c.rollback();
            db.disconnect(c, s, r);
        }
 
        return idMacro;
    }
    

    /**
     * Update macro
     * 
     * @param idUser id of the user
     * @param macro macro to update
     * @throws SQLException
     */
    private int updateMacro(int idUser, MacroInterface macro) throws SQLException {
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        try {
            c = db.connect();

            // delete macro actions
            String query3 = "DELETE FROM actionmacrolist WHERE idmacro = ?";
            s = c.prepareStatement(query3);
            s.setInt(1, macro.getId());
            s.execute();
            
            s = null;
            
            c.setAutoCommit(false);
            List<Action> actions = macro.getActions();
            int idTrigger = getTrigger(macro.getTrigger().getName(), macro.getTrigger().getMinVersion(), macro.getTrigger().getFormalParameters()); 
            int idDevice = macro.getTrigger().getExecutionDevice().getId();
            
            String query = "UPDATE macro " +
                           "SET name=?, iduser=?, triggerjson=?, iddevice=?, idtrigger=?, icon=?, color=?, incomplete=?, errorpolicy=? " + 
                           "WHERE id = ?";
            
            s = c.prepareStatement(query);
            s.setString(1, macro.getName());
            s.setInt(2, idUser);
            s.setString(3, macro.getTrigger().getActualParameters());
            s.setInt(4, idDevice);
            s.setInt(5, idTrigger);
            s.setString(6, macro.getIconName());
            s.setString(7, macro.getMacroColor());
            s.setInt(8, macro.isValid() ? 0 : 1);
            s.setString(9, macro.getExecutionFailurePolicy());
            s.setInt(10, macro.getId());
            s.execute();           
            
            // add actions
            String query2 = "INSERT INTO actionmacrolist(idmacro, idaction, actionjson, iddevice, counter)" +
                            "VALUES(?, ?, ?, ?, ?)";
            int counter = 0;
            
            for(Action action : actions) {
                s = null;
                r = null;
                s = c.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);
                s.setInt(1, macro.getId());
                s.setInt(2, getIdAction(action.getName(), action.getMinVersion(), action.getFormalParameters()));
                s.setString(3, action.getActualParameters());
                s.setInt(4, action.getExecutionDevice().getId());
                s.setInt(5, counter++);
                s.execute();   
                r = s.getGeneratedKeys();
                r.next();
            } 

            
            c.commit();
            db.disconnect(c, s);  
               
        } catch(Exception e) {
            // in case of error, rollback the changes and disconnect
            e.printStackTrace();
            c.rollback();
            db.disconnect(c, s);
            throw e;
        }
         
        return macro.getId();
    }   
}
