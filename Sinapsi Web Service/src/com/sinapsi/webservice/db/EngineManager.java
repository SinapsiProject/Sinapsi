package com.sinapsi.webservice.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.sinapsi.model.MacroComponent;

/**
 * Class that perform engine(triggers, actions, macros) query
 *
 */
public class EngineManager {
	private DatabaseController db;
	
	/**
	 * Default ctor
	 */
	public EngineManager() {
		db = new DatabaseController();
	}
	
	/**
     * Return the available Actions offered by a specific device
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

	public void addAvailabeTriggers(int idDevice, List<MacroComponent> triggers) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
