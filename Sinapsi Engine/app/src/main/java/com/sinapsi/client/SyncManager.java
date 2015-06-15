package com.sinapsi.client;

import com.sinapsi.client.persistence.DiffDBManager;
import com.sinapsi.client.persistence.LocalDBManager;
import com.sinapsi.client.web.SinapsiWebServiceFacade;
import com.sinapsi.model.MacroInterface;

import java.util.List;

/**
 * Manages the sync of the data between the local database
 * and the database in the Web Server.
 */
public class SyncManager {

    private SinapsiWebServiceFacade webService;
    private LocalDBManager lastSyncDb;
    private LocalDBManager currentDb;
    private DiffDBManager diffDb;

    public SyncManager(SinapsiWebServiceFacade webService,
                       LocalDBManager lastSyncDb,
                       LocalDBManager currentDb,
                       DiffDBManager diffDb) {
        this.webService = webService;
        this.lastSyncDb = lastSyncDb;
        this.currentDb = currentDb;
        this.diffDb = diffDb;
    }


    /**
     * Adds a new macro and the related actions to the current db,
     * or updates them if the macro's id is equal to another macro
     * already in the db.
     *
     * @param macro the macro to be inserted/updated
     */
    public void addOrUpdateMacro(MacroInterface macro){
        if(currentDb.addOrUpdateMacro(macro)){
            diffDb.macroAdded(macro);
        }else{
            diffDb.macroUpdated(macro);
        }
    }

    /**
     * Retrieves all the macros saved in the current db and returns
     * them.
     *
     * @return a list of macros
     */
    public List<MacroInterface> getAllMacros(){
        return currentDb.getAllMacros();
    }

    /**
     * Removes the macro with the specified id from the current db.
     *
     * @param id the macro id
     */
    public void removeMacro(int id){
        currentDb.removeMacro(id);
        diffDb.macroRemoved(id);
    }

    /**
     * Deletes all the rows from all the tables in the DBs.
     */
    public void clearAll(){
        currentDb.clearDB();
        lastSyncDb.clearDB();
        diffDb.clearDB();
    }

    public void sync(){
        List<MacroInterface> serverMacros = webService.getAllMacros();
        //TODO: impl
    }

    public int getMinId() {
        return currentDb.getMinMacroId();
    }
}
