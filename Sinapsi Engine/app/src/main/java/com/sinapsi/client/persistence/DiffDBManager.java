package com.sinapsi.client.persistence;

import com.sinapsi.client.persistence.syncmodel.MacroChange;
import com.sinapsi.model.MacroInterface;

import java.util.List;

/**
 * This interface contains a collection
 * of methods useful to track changes
 * in a db;
 *
 */
public interface DiffDBManager {

    /**
     * Updates the db to keep track of the adding of a new macro
     *
     * @param macro the added macro
     */
    void macroAdded(MacroInterface macro) throws InconsistentMacroChangeException;

    /**
     * Updates the db to keep track of the update of a macro
     *
     * @param macro the updated macro
     */
    void macroUpdated(MacroInterface macro) throws InconsistentMacroChangeException;

    /**
     * Updates the db to keep track of the removal of a macro
     *
     * @param id the id of the removed macro
     */
    void macroRemoved(int id) throws InconsistentMacroChangeException;

    /**
     * Extracts all the changes from the related table and returns them
     *
     * @return a list of changes
     */
    public List<MacroChange> getAllChanges();

    /**
     * Clears all the tables in the db.
     */
    public void clearDB();

    /**
     * Extracts all the changes for the macro with the specified id
     *
     * @param id the macro id
     * @return the list of changes
     */
    public MacroChange getChangeForMacro(int id); //TODO: let this return only a macro change

    /**
     * Returns the smallest negative macro id, or 0 if there are no negative ids.
     * @return the smallest macro id
     */
    public int getMinMacroId();

}
