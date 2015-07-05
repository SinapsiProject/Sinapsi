package com.sinapsi.client;

import com.sinapsi.client.persistence.DiffDBManager;
import com.sinapsi.client.persistence.InconsistentMacroChangeException;
import com.sinapsi.client.persistence.LocalDBManager;
import com.sinapsi.client.persistence.MemoryDiffDBManager;
import com.sinapsi.client.persistence.MemoryLocalDBManager;
import com.sinapsi.client.persistence.syncmodel.MacroChange;
import com.sinapsi.client.persistence.syncmodel.MacroSyncConflict;
import com.sinapsi.client.web.SinapsiWebServiceFacade;
import com.sinapsi.engine.Action;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.model.impl.SyncOperation;
import com.sinapsi.utils.Pair;
import com.sinapsi.utils.Triplet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        public void onSyncSuccess(List<MacroInterface> currentMacros);
        public void onSyncConflicts(List<MacroSyncConflict> conflicts, ConflictResolutionCallback conflictCallback);
        public void onSyncFailure(Throwable error);
    }


    public interface ConflictResolutionCallback {
        public void onConflictsResolved(List<MacroChange> toBePushedConflict, List<MacroChange> toBePulledConflict);
        public void onAbort();
    }

    private SinapsiWebServiceFacade webService;
    private LocalDBManager lastSyncDb;
    private LocalDBManager currentDb;
    private DiffDBManager diffDb;
    private DeviceInterface device;

    public SyncManager(SinapsiWebServiceFacade webService,
                       LocalDBManager lastSyncDb,
                       LocalDBManager currentDb,
                       DiffDBManager diffDb,
                       DeviceInterface device) {

        this.webService = webService;
        this.lastSyncDb = lastSyncDb;
        this.currentDb = currentDb;
        this.diffDb = diffDb;
        this.device = device;
    }


    /**
     * Adds a new macro and the related actions to the current db
     * @param macro the macro to be inserted
     * @throws InconsistentMacroChangeException todo doku
     */
    public void addMacro(MacroInterface macro) throws InconsistentMacroChangeException {
        diffDb.macroAdded(macro);
        currentDb.addOrUpdateMacro(macro);  //Hint: check consistency from the returned boolean
    }

    /**
     * Updates a macro if its id is equal to another macro's id
     * already in the db.
     * @param macro the macro to be updated
     * @throws InconsistentMacroChangeException todo doku
     */
    public void updateMacro(MacroInterface macro) throws InconsistentMacroChangeException {
        diffDb.macroUpdated(macro);
        currentDb.addOrUpdateMacro(macro);  //Hint: check consistency from the returned boolean
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
     * @throws InconsistentMacroChangeException todo doku
     */
    public void removeMacro(int id) throws InconsistentMacroChangeException {
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
        if(AppConsts.DEBUG_DISABLE_SYNC){
            callback.onSyncSuccess(currentDb.getAllMacros());
            return;
        }
        webService.getAllMacros(device, new SinapsiWebServiceFacade.WebServiceCallback<Pair<Boolean, List<MacroInterface>>>() {
            @Override
            public void success(Pair<Boolean, List<MacroInterface>> result, Object response) {
                final List<MacroInterface> serverMacros = result.getSecond();

                if (result.getFirst()) {
                    syncWithFreshDataFromServer(callback, serverMacros);
                } else {
                    if (diffDb.getAllChanges().isEmpty()) {
                        //server and client probably have same data, but let's do
                        //another check on the number of macros to see if this is true
                        if (currentDb.getAllMacros().size() != serverMacros.size()) {
                            //something in the change tracking system has gone wrong
                            System.out.println("SINAPSI SYNC MANAGER: The change tracking mechanism failed, the server returned a false negative sync flag");
                            //forcing to sync as if the boolean was true
                            syncWithFreshDataFromServer(callback, serverMacros);
                        }
                        callback.onSyncSuccess(currentDb.getAllMacros());
                    } else {
                        //only the client has updated data
                        List<MacroChange> toBePushed = diffDb.getAllChanges();
                        final List<Pair<SyncOperation, MacroInterface>> pushtmp = convertChangesToPushSyncOps(toBePushed, currentDb);
                        webService.pushChanges(
                                device,
                                convertChangesToPushSyncOps(diffDb.getAllChanges(), currentDb),
                                new PushAndPullWebServiceCallBack(callback, pushtmp, null, null, new int[]{0, 0}, null, 0));
                    }
                }
            }

            @Override
            public void failure(Throwable error) {
                callback.onSyncFailure(error);
            }
        });
    }


    private void syncWithFreshDataFromServer(final MacroSyncCallback callback, final List<MacroInterface> serverMacros){
        if (diffDb.getAllChanges().isEmpty()) {
            //only the server have new data, so just clear local db and pull everything
            clearAll();
            for (MacroInterface mi : serverMacros) {
                currentDb.addOrUpdateMacro(mi);
                lastSyncDb.addOrUpdateMacro(mi);
            }
            callback.onSyncSuccess(serverMacros);
            return;
        } else {
            //checks all the differences between the macro collection in the server
            //  and the macros that were in the last sync
            MemoryDiffDBManager diffServer_OldCopy = new MemoryDiffDBManager();
            for (MacroInterface serverMacro : serverMacros) {
                if (lastSyncDb.containsMacro(serverMacro.getId())) {

                    MacroInterface oldCopyMacro = lastSyncDb.getMacroWithId(serverMacro.getId());

                    if (!areMacrosEqual(oldCopyMacro, serverMacro)) {
                        try {
                            diffServer_OldCopy.macroUpdated(serverMacro);
                        } catch (InconsistentMacroChangeException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        diffServer_OldCopy.macroAdded(serverMacro);
                    } catch (InconsistentMacroChangeException e) {
                        e.printStackTrace();
                    }
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
                    try {
                        diffServer_OldCopy.macroRemoved(oldCopyMacro.getId());
                    } catch (InconsistentMacroChangeException e) {
                        e.printStackTrace();
                    }
                }
            }

            final Triplet<Pair<Map<Integer, MacroChange>, Map<Integer, MacroChange>>, List<MacroSyncConflict>, Integer> diffsAnalysisResults; //TODO: define specific class for this
            diffsAnalysisResults = analyzeDiffs(currentDb.getAllMacros(), serverMacros, diffServer_OldCopy, diffDb);

            final Map<Integer, MacroChange> toBePushed = diffsAnalysisResults.getFirst().getSecond();
            final Map<Integer, MacroChange> toBePulled = diffsAnalysisResults.getFirst().getFirst();
            final int[] pushedAndPulledCounters = new int[]{0, 0};

            List<MacroSyncConflict> conflicts = diffsAnalysisResults.getSecond();
            final Integer noChangesCount = diffsAnalysisResults.getThird();
            if (conflicts.isEmpty()) {
                //there are no conflicts,
                //proceeds directly with push and pull
                final List<Pair<SyncOperation, MacroInterface>> pushtmp = convertChangesToPushSyncOps(toBePushed.values(), currentDb);
                webService.pushChanges(
                        device,
                        pushtmp,
                        new PushAndPullWebServiceCallBack(
                                callback,
                                pushtmp,
                                toBePulled,
                                serverMacros,
                                pushedAndPulledCounters,
                                noChangesCount,
                                0)
                );
            } else {
                callback.onSyncConflicts(conflicts, new ConflictResolutionCallback() {
                    @Override
                    public void onConflictsResolved(final List<MacroChange> toBePushedConflicts, final List<MacroChange> toBePulledConflicts) {


                        for (MacroChange conflictChange : toBePushedConflicts) {
                            toBePushed.put(conflictChange.getMacroId(), conflictChange);
                        }
                        for (MacroChange conflictChange : toBePulledConflicts) {
                            toBePulled.put(conflictChange.getMacroId(), conflictChange);
                        }

                        final List<Pair<SyncOperation, MacroInterface>> pushtmp = convertChangesToPushSyncOps(toBePushed.values(), currentDb);
                        webService.pushChanges(
                                device,
                                pushtmp,
                                new PushAndPullWebServiceCallBack(
                                        callback,
                                        pushtmp,
                                        toBePulled,
                                        serverMacros,
                                        pushedAndPulledCounters,
                                        noChangesCount,
                                        toBePushedConflicts.size() + toBePulledConflicts.size())
                        );
                    }

                    @Override
                    public void onAbort() {
                        //abort by user (no changes on both client and server)
                        return;
                    }
                });
            }
        }
    }


    private class PushAndPullWebServiceCallBack implements SinapsiWebServiceFacade.WebServiceCallback<List<Pair<SyncOperation, Integer>>> {
        private final MacroSyncCallback callback;
        private final List<Pair<SyncOperation, MacroInterface>> pushtmp;
        private final Map<Integer, MacroChange> toBePulled;
        private final List<MacroInterface> serverMacros;
        private final int[] pushedAndPulledCounters;
        private final Integer noChangesCount;
        private final Integer conflictsCount;


        public PushAndPullWebServiceCallBack(
                MacroSyncCallback callback,
                List<Pair<SyncOperation, MacroInterface>> pushtmp,
                Map<Integer, MacroChange> toBePulled,
                List<MacroInterface> serverMacros,
                int[] pushedAndPulledCounters,
                Integer noChangesCount,
                Integer conflictsCount) {
            this.callback = callback;
            this.pushtmp = pushtmp;
            this.pushedAndPulledCounters = pushedAndPulledCounters;
            this.toBePulled = toBePulled;
            this.serverMacros = serverMacros;
            this.noChangesCount = noChangesCount;
            this.conflictsCount = conflictsCount;
        }

        @Override
        public void success(List<Pair<SyncOperation, Integer>> pushResult, Object response) {
            if (pushResult == null || (pushResult.size() == 1 && pushResult.get(0).isErrorOccured())) {
                System.out.println("SYNC: Error occurred during sync: " + pushResult.get(0).getErrorDescription());
                callback.onSyncFailure(new SyncServerException(pushResult.get(0).getErrorDescription()));
                //abort (no changes on both client and server)
                return;

            } else {
                MemoryLocalDBManager tempDB = new MemoryLocalDBManager(currentDb);
                tempDB.deleteMacrosWithNegativeId();
                for (int i = 0; i < pushResult.size(); ++i) {
                    if (pushResult.get(i).getFirst() == SyncOperation.ADD) {
                        int newId = pushResult.get(i).getSecond();
                        MacroInterface mi = pushtmp.get(i).getSecond();
                        mi.setId(newId);
                        tempDB.addOrUpdateMacro(mi);
                    }
                    ++pushedAndPulledCounters[0];
                }

                if(toBePulled != null) {
                    //HINT: take advantage of parallelism (move the pull just after the push call),
                    // so the service and the client can work at the same time
                    for (MacroChange macroChange : toBePulled.values()) {
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
                }

                commit(tempDB);

                callback.onSyncSuccess(currentDb.getAllMacros());

            }

        }

        @Override
        public void failure(Throwable error) {
            callback.onSyncFailure(error);
            //abort (no changes on client, and
            // very probably on server too, but
            // server may have received data and
            // updated its DB correctly, and
            // only the response caused the
            // error) todo: handle this case
            /*
            Possibile modo di risolvere:
                1) il client fa commit
                2) avvia un timer (massimo 15 secondi), lo stesso che
                    c'è sul server
                3) tenta di fare la conferma
                4.a) se ha successo, ok
                4.b) se fallisce, riprova a fare la conferma
                finché questa non ha successo oppure non scade il timer
                4.c) se scade il timer, rollback dai backup
             */
            return;
        }
    }



    private List<Pair<SyncOperation, MacroInterface>> convertChangesToPushSyncOps(Collection<MacroChange> toBePushed, LocalDBManager db) {
        List<Pair<SyncOperation, MacroInterface>> result = new ArrayList<>();

        for(MacroChange mc:toBePushed){
            switch (mc.getChangeType()){

                case ADDED:
                    result.add(new Pair<>(SyncOperation.ADD, db.getMacroWithId(mc.getMacroId())));
                    break;
                case EDITED:
                    result.add(new Pair<>(SyncOperation.UPDATE, db.getMacroWithId(mc.getMacroId())));
                    break;
                case REMOVED:
                    result.add(new Pair<>(SyncOperation.DELETE, fm.newMacro("", mc.getMacroId())));
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
        return Math.min(currentDb.getMinMacroId(), diffDb.getMinMacroId());
    }

    private Triplet<Pair<Map<Integer, MacroChange>, Map<Integer, MacroChange>>, List<MacroSyncConflict>, Integer>
    analyzeDiffs(List<MacroInterface> localDBMacros,
                 List<MacroInterface> serverMacros,
                 DiffDBManager serverChanges,
                 DiffDBManager clientChanges) {
        List<MacroSyncConflict> conflicts = new ArrayList<>();
        Map<Integer, MacroChange> toBePulled = new HashMap<>();
        Map<Integer, MacroChange> toBePushed = new HashMap<>();
        List<Integer> allInterestedMacroIDs = new ArrayList<>();

        //get all the IDs of the interested macros, both on server and client,
        // and add them in a list, with no duplicates
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
            MacroChange serverMacroChange = serverChanges.getChangeForMacro(i);
            MacroChange localMacroChange = clientChanges.getChangeForMacro(i);

            boolean localCheck = localMacroChange != null;
            boolean serverCheck = serverMacroChange != null;

            if (serverCheck && !localCheck) {
                //the interested macro has relevant changes only on the server (= no conflicts)
                //there are changes to be pulled
                toBePulled.put(serverMacroChange.getMacroId(), serverMacroChange);

            } else if (!serverCheck && localCheck) {
                //the interested macro has relevant changes only on the client (= no conflicts)
                //there are changes to be pushed
                toBePushed.put(localMacroChange.getMacroId(), localMacroChange);

            } else //noinspection ConstantConditions
                if (serverCheck && localCheck) {
                    //the interested macro has relevant changes both on the server and the client
                    //there are conflicts in changes

                    //some conflicts are solved automatically
                    if (serverMacroChange.getChangeType() == MacroChange.ChangeTypes.REMOVED &&
                            localMacroChange.getChangeType() == MacroChange.ChangeTypes.REMOVED) {
                        // if the macro has been deleted both on client and server, ignore.
                        ++noChangesCount;
                        continue;
                    } else if (serverMacroChange.getChangeType() == MacroChange.ChangeTypes.EDITED &&
                            localMacroChange.getChangeType() == MacroChange.ChangeTypes.EDITED) {
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
                            serverMacroChange,
                            localMacroChange));

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
