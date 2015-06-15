package com.sinapsi.client;

import com.sinapsi.client.persistence.DiffDBManager;
import com.sinapsi.client.persistence.LocalDBManager;
import com.sinapsi.client.persistence.MemoryDiffDBManager;
import com.sinapsi.client.persistence.syncmodel.MacroSyncConflict;
import com.sinapsi.client.web.SinapsiWebServiceFacade;
import com.sinapsi.model.MacroInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

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
        webService.getAllMacros(new SinapsiWebServiceFacade.WebServiceCallback<List<MacroInterface>>() {
            @Override
            public void success(List<MacroInterface> serverMacros, Object response) {

                //checks all the differences between the macro collection in the server
                //  and the macros that were in the last sync
                MemoryDiffDBManager diffServer_OldCopy = new MemoryDiffDBManager();
                for (MacroInterface serverMacro : serverMacros) {
                    if (lastSyncDb.containsMacro(serverMacro.getId())) {

                        MacroInterface oldCopyMacro = lastSyncDb.getMacroWithId(serverMacro.getId());

                        if (!areMacrosEqual(oldCopyMacro, serverMacro)) {
                            diffServer_OldCopy.macroUpdated(serverMacro);
                        }
                    } else {
                        diffServer_OldCopy.macroAdded(serverMacro);
                    }


                }

                for (MacroInterface oldCopyMacro : lastSyncDb.getAllMacros()) {
                    boolean found = false;
                    for (MacroInterface serverMacro : serverMacros) {
                        if (oldCopyMacro.getId() == serverMacro.getId()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        diffServer_OldCopy.macroRemoved(oldCopyMacro.getId());
                    }
                }

                List<MacroSyncConflict> conflicts = findSyncConflicts(diffServer_OldCopy, diffDb);


            }

            @Override
            public void failure(Throwable error) {
                //TODO: impl
                RetrofitError err = (RetrofitError) error;

                switch (err.getKind()) {
                    case NETWORK:
                        break;
                    case CONVERSION:
                        break;
                    case HTTP:
                        break;
                    case UNEXPECTED:
                        break;
                }
            }
        });
    }

    public boolean areMacrosEqual(MacroInterface m1, MacroInterface m2){
        //TODO: impl
        return true;
    }

    public int getMinId() {
        return currentDb.getMinMacroId();
    }

    private List<MacroSyncConflict> findSyncConflicts(DiffDBManager serverChanges, DiffDBManager clientChanges){
        List<MacroSyncConflict> result = new ArrayList<>();
        //TODO: impl

        return result;

    }
}
