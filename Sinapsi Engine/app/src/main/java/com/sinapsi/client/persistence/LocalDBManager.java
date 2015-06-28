package com.sinapsi.client.persistence;

import com.sinapsi.model.MacroInterface;

import java.util.List;

/**
 * LocalDBFacade interface. This interface contains a collection
 * of methods useful to interact with persistent model data.
 */
public interface LocalDBManager {
    /**
     * Adds a new macro and the related actions to the db, or
     * updates them if the macro's id is equal to another macro
     * already in the db.
     *
     * @param macro the macro to be inserted/updated
     * @return true if the macro has been added, false if there has been
     * an update
     */
    public boolean addOrUpdateMacro(MacroInterface macro);

    /**
     * Retrieves all the macros saved in the db and returns them.
     *
     * @return a list of macros
     */
    public List<MacroInterface> getAllMacros();

    /**
     * Removes the macro with the specified id from the db.
     *
     * @param id the macro id
     */
    public void removeMacro(int id);

    /**
     * Deletes all the rows from all the tables in the db.
     */
    public void clearDB();

    /**
     * Gets the minimum of the macro's negative ids, or 0 if there are no negative ids.
     *
     * @return the minimum id
     */
    public int getMinMacroId();

    /**
     * Checks if the macro with the given id is in the DB
     *
     * @param id the macro id
     * @return true if the macro is in the db, false otherwise
     */
    public boolean containsMacro(int id);

    /**
     * Returns the macro with the specified id
     *
     * @param id the macro id
     * @return the macro with the specified id, if it exists in the db,
     * or null otherwise
     */
    public MacroInterface getMacroWithId(int id);

    /**
     * Deletes all the macros with negative id, which usually are new macros
     * that are not synced with the server yet.
     */
    public void deleteMacrosWithNegativeId();
}
