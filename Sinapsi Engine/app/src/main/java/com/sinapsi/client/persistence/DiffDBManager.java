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
    void macroAdded(MacroInterface macro);

    /**
     * Updates the db to keep track of the update of a macro
     *
     * @param macro the updated macro
     */
    void macroUpdated(MacroInterface macro);

    /**
     * Updates the db to keep track of the removal of a macro
     *
     * @param id the id of the removed macro
     */
    void macroRemoved(int id);

    /**
     * Extracts all the changes from the related table and returns them
     *
     * @return a list of changes
     */
    public List<MacroChange> getAllChanges();

    /**
     * Clears all the tables in the db.
     */
    void clearDB();
}
