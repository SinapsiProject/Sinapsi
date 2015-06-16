package com.sinapsi.client;

import com.sinapsi.client.persistence.DiffDBManager;
import com.sinapsi.client.persistence.LocalDBManager;
import com.sinapsi.client.persistence.MemoryDiffDBManager;
import com.sinapsi.client.persistence.MemoryLocalDBManager;
import com.sinapsi.client.persistence.syncmodel.MacroChange;
import com.sinapsi.client.persistence.syncmodel.MacroSyncConflict;
import com.sinapsi.client.web.SinapsiWebServiceFacade;
import com.sinapsi.engine.Action;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.model.impl.SyncOperation;
import com.sinapsi.utils.Pair;
import com.sinapsi.utils.Triplet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the sync of the data between the local database
 * and the database in the Web Server.
 */
public class SyncManager {

    private static FactoryModel fm = new FactoryModel();

    /**
     * Interface exposed from the sync manager in order to manage
     * the async nature of the sync() method.
     */
    public interface MacroSyncCallback {
        public void onSuccess(Integer pushed, Integer pulled, Integer noChanged, Integer resolvedConflicts);

        public void onConflicts(List<MacroSyncConflict> conflicts, ConflictResolutionCallback conflictCallback);

        public void onFailure(Throwable error);
    }


    public interface ConflictResolutionCallback {
        public void onConflictsResolved(List<MacroChange> toBePushedConflict, List<MacroChange> toBePulledConflict);
        public void onAbort();
    }

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
    public void addOrUpdateMacro(MacroInterface macro) {
        if (currentDb.addOrUpdateMacro(macro)) {
            diffDb.macroAdded(macro);
        } else {
            diffDb.macroUpdated(macro);
        }
    }

    /**
     * Retrieves all the macros saved in the current db and returns
     * them.
     *
     * @return a list of macros
     */
    public List<MacroInterface> getAllMacros() {
        return currentDb.getAllMacros();
    }

    /**
     * Removes the macro with the specified id from the current db.
     *
     * @param id the macro id
     */
    public void removeMacro(int id) {
        currentDb.removeMacro(id);
        diffDb.macroRemoved(id);
    }

    /**
     * Deletes all the rows from all the tables in the DBs.
     */
    public void clearAll() {
        currentDb.clearDB();
        lastSyncDb.clearDB();
        diffDb.clearDB();
    }

    public void sync(final MacroSyncCallback callback) {

        webService.getAllMacros(new SinapsiWebServiceFacade.WebServiceCallback<Pair<Boolean, List<MacroInterface>>>() {
            @Override
            public void success(Pair<Boolean, List<MacroInterface>> result, Object response) {
                final List<MacroInterface> serverMacros = result.getSecond();
                if (result.getFirst()) {
                    if (diffDb.getAllChanges().isEmpty()) {
                        clearAll();
                        for (MacroInterface mi : serverMacros) {
                            currentDb.addOrUpdateMacro(mi);
                            lastSyncDb.addOrUpdateMacro(mi);
                            callback.onSuccess(0, null, null, null);
                            return;
                        }
                    } else {
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

                        final Triplet<Pair<List<MacroChange>, List<MacroChange>>, List<MacroSyncConflict>, Integer> diffsAnalysisResults;
                        diffsAnalysisResults = analyzeDiffs(currentDb.getAllMacros(), serverMacros, diffServer_OldCopy, diffDb);


                        List<MacroSyncConflict> conflicts = diffsAnalysisResults.getSecond();
                        final Integer noChangesCount = diffsAnalysisResults.getThird();
                        if (conflicts.isEmpty()) {
                            //there are no conflicts,
                            //TODO: proceed directly with push and pull
                        } else {
                            callback.onConflicts(conflicts, new ConflictResolutionCallback() {
                                @Override
                                public void onConflictsResolved(final List<MacroChange> toBePushedConflicts, final List<MacroChange> toBePulledConflicts) {

                                    List<MacroChange> toBePushed = diffsAnalysisResults.getFirst().getSecond();
                                    final List<MacroChange> toBePulled = diffsAnalysisResults.getFirst().getFirst();
                                    final int[] pushedAndPulledCounters = new int[]{0, 0};

                                    toBePushed.addAll(toBePushedConflicts);
                                    toBePulled.addAll(toBePulledConflicts);

                                        Collections.sort(toBePushed);
                                    final List<Pair<SyncOperation, MacroInterface>> pushtmp = convertChangesToPushSyncOps(toBePushed, currentDb);
                                    webService.pushChanges(
                                            null, //TODO: set device
                                            pushtmp,
                                            new SinapsiWebServiceFacade.WebServiceCallback<List<Pair<SyncOperation, Integer>>>() {
                                                @Override
                                                public void success(List<Pair<SyncOperation, Integer>> pushResult, Object response) {
                                                    if (pushResult == null || (pushResult.size() == 1 && pushResult.get(0).isErrorOccured())) {
                                                        System.out.println("SYNC: Error occurred during sync: " + pushResult.get(0).getErrorDescription());
                                                        callback.onFailure(new SyncServerException(pushResult.get(0).getErrorDescription()));
                                                        //rollback (no changes on both client and server)
                                                        return;

                                                    } else {
                                                        MemoryLocalDBManager tempDB = new MemoryLocalDBManager(currentDb);
                                                        //TODO: delete all macros with negative id fromTempDB
                                                        for (int i = 0; i < pushResult.size(); ++i) {
                                                            if (pushResult.get(i).getFirst() == SyncOperation.ADD) {
                                                                int newId = pushResult.get(i).getSecond();
                                                                MacroInterface mi = pushtmp.get(i).getSecond();
                                                                mi.setId(newId);
                                                                tempDB.addOrUpdateMacro(mi);
                                                            }
                                                            ++pushedAndPulledCounters[0];
                                                        }

                                                        //HINT: take advantage of parallelism (move the pull just after the push call)
                                                        Collections.sort(toBePulled);
                                                        for (MacroChange macroChange : toBePulled) {
                                                            //saves data from the server in the db
                                                            switch (macroChange.getChangeType()) {
                                                                case ADDED:
                                                                case EDITED:
                                                                    tempDB.addOrUpdateMacro(getMacroFromList(serverMacros, macroChange.getId()));
                                                                    break;
                                                                case REMOVED:
                                                                    tempDB.removeMacro(macroChange.getId());
                                                                    break;
                                                            }
                                                            //increments pull counter
                                                            ++pushedAndPulledCounters[1];
                                                        }

                                                        commit(tempDB);

                                                        callback.onSuccess(
                                                                pushedAndPulledCounters[0],
                                                                pushedAndPulledCounters[1],
                                                                noChangesCount,
                                                                toBePushedConflicts.size() + toBePulledConflicts.size());

                                                    }

                                                }

                                                @Override
                                                public void failure(Throwable error) {
                                                    callback.onFailure(error);
                                                    //rollback (no changes on client,
                                                    // server may have received data but
                                                    // maybe the only the response caused
                                                    // the error) todo: handle this case
                                                    return;
                                                }
                                            }
                                    );
                                }

                                @Override
                                public void onAbort() {
                                    //rollback by user (no changes on both client and server)
                                    return;
                                }
                            });
                        }
                    }

                } else {
                    if (diffDb.getAllChanges().isEmpty()) {
                        //server and client probably have same data, but let's do
                        //another check on the number of macros to see if this is true
                        if (currentDb.getAllMacros().size() == serverMacros.size()) {
                            //no changes.
                        } else {
                            //something in the change tracking system has gone wrong
                            throw new SyncServerException("The change tracking mechanism failed");
                        }
                    } else {
                        //only the client has updated data
                        //TODO: just push changes
                    }
                }
            }

            @Override
            public void failure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }

    private List<Pair<SyncOperation, MacroInterface>> convertChangesToPushSyncOps(List<MacroChange> toBePushed, LocalDBManager db) {
        List<Pair<SyncOperation, MacroInterface>> result = new ArrayList<>();

        for(MacroChange mc:toBePushed){
            switch (mc.getChangeType()){

                case ADDED:
                    result.add(new Pair<>(SyncOperation.ADD, db.getMacroWithId(mc.getId())));
                    break;
                case EDITED:
                    result.add(new Pair<>(SyncOperation.ADD, db.getMacroWithId(mc.getId())));
                    break;
                case REMOVED:
                    result.add(new Pair<>(SyncOperation.ADD, fm.newMacro("", mc.getId())));
                    break;
            }
        }

        return result;
    }

    public boolean areMacrosEqual(MacroInterface m1, MacroInterface m2) {
        if (m1.getId() != m2.getId()) return false;
        if (!m1.getName().equals(m2.getName())) return false;
        if (!m1.getIconName().equals(m2.getIconName())) return false;
        if (!m1.getMacroColor().equals(m2.getMacroColor())) return false;
        if (!m1.getExecutionFailurePolicy().equals(m2.getExecutionFailurePolicy())) return false;
        if (!m1.getTrigger().getName().equals(m2.getTrigger().getName())) return false;
        if (!m1.getTrigger().getActualParameters().equals(m2.getTrigger().getActualParameters()))
            return false;
        if (m1.getTrigger().getExecutionDevice().getId() != m2.getTrigger().getExecutionDevice().getId())
            return false;
        if (m1.getActions().size() != m2.getActions().size()) return false;
        for (int i = 0; i < m1.getActions().size(); i++) {
            Action a1 = m1.getActions().get(i);
            Action a2 = m2.getActions().get(i);
            if (a1.getExecutionDevice() != a2.getExecutionDevice()) return false;
            if (!a1.getActualParameters().equals(a2.getActualParameters())) return false;
            if (!a1.getName().equals(a2.getName())) return false;
        }
        return true;
    }

    public int getMinId() {
        return currentDb.getMinMacroId();
    }

    private Triplet<Pair<List<MacroChange>, List<MacroChange>>, List<MacroSyncConflict>, Integer>
    analyzeDiffs(List<MacroInterface> localDBMacros,
                 List<MacroInterface> serverMacros,
                 DiffDBManager serverChanges,
                 DiffDBManager clientChanges) {
        List<MacroSyncConflict> conflicts = new ArrayList<>();
        List<MacroChange> toBePulled = new ArrayList<>();
        List<MacroChange> toBePushed = new ArrayList<>();
        List<Integer> allInterestedMacroIDs = new ArrayList<>();

        //get all the IDs of the interested macros, both on server and client,
        // and add them in a no-duplicate list
        for (MacroInterface m : localDBMacros) {
            allInterestedMacroIDs.add(m.getId());
        }
        for (MacroInterface m : serverMacros) {
            if (!allInterestedMacroIDs.contains(m.getId()))
                allInterestedMacroIDs.add(m.getId());
        }
        int noChangesCount = 0;
        //now extract eventual changes for every macro
        for (Integer i : allInterestedMacroIDs) {
            List<MacroChange> serverMacroChanges = serverChanges.getChangesForMacro(i);
            List<MacroChange> localMacroChanges = clientChanges.getChangesForMacro(i);

            boolean localCheck;
            if (!localMacroChanges.isEmpty()) {
                if (localMacroChanges.get(0).getChangeType() == MacroChange.ChangeTypes.ADDED &&
                        localMacroChanges.get(localMacroChanges.size() - 1).getChangeType() == MacroChange.ChangeTypes.REMOVED) {
                    //if a macro has been added and at the end deleted, ignore
                    localCheck = false;
                } else {
                    localCheck = true;
                }
            } else {
                localCheck = false;
            }

            boolean serverCheck = !serverMacroChanges.isEmpty();

            if (serverCheck && !localCheck) {
                //the interested macro has relevant changes only on the server (= no conflicts)
                //there are changes to be pulled
                toBePulled.addAll(serverMacroChanges);
            } else if (!serverCheck && localCheck) {
                //the interested macro has relevant changes only on the client (= no conflicts)
                //there are changes to be pushed
                toBePushed.addAll(localMacroChanges);
            } else //noinspection ConstantConditions
                if (serverCheck && localCheck) {
                    //the interested macro has relevant changes both on the server and the client
                    //there are conflicts in changes

                    //some conflicts are solved automatically
                    if (serverMacroChanges.get(serverMacroChanges.size() - 1).getChangeType() == MacroChange.ChangeTypes.REMOVED &&
                            localMacroChanges.get(localMacroChanges.size() - 1).getChangeType() == MacroChange.ChangeTypes.REMOVED) {
                        // if the macro has been deleted both on client and server, ignore.
                        ++noChangesCount;
                        continue;
                    } else if (serverMacroChanges.get(serverMacroChanges.size() - 1).getChangeType() == MacroChange.ChangeTypes.EDITED &&
                            localMacroChanges.get(localMacroChanges.size() - 1).getChangeType() == MacroChange.ChangeTypes.EDITED) {
                        if (areMacrosEqual(getMacroFromList(serverMacros, i), getMacroFromList(localDBMacros, i))) {
                            /* if the macro has been edited both on client and server, and
                           the two copies of the macro are equal, ignore*/
                            ++noChangesCount;
                            continue;
                        }

                    }


                    //----: Conflicts that can't be solved: 1) edit on A and delete on B
                    //----:    2) edit on A and edit on B but macros are not longer the same
                    //----:         ---> ask the user what to do

                    MacroInterface serverMacro = serverMacros.get(i);
                    MacroInterface localMacro = localDBMacros.get(i);

                    conflicts.add(new MacroSyncConflict(
                            serverMacro,
                            localMacro,
                            serverMacroChanges,
                            localMacroChanges));

                } else {
                    //no changes on this macro (= no conflicts)
                    ++noChangesCount;
                }

        }

        return new Triplet<>(new Pair<>(toBePulled, toBePushed), conflicts, noChangesCount);

    }

    public void commit(MemoryLocalDBManager tempDB){
        clearAll();
        tempDB.saveToDb(currentDb,false);
        tempDB.saveToDb(lastSyncDb,false);
    }

    private static MacroInterface getMacroFromList(List<MacroInterface> list, int id) {
        for (MacroInterface m : list) {
            if (m.getId() == id) return m;
        }
        return null;
    }

    public class SyncServerException extends RuntimeException{
        public SyncServerException(){
            super();
        }

        public SyncServerException(String message){
            super(message);
        }
    }



}
