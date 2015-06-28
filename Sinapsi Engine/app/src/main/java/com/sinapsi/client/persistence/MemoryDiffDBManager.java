package com.sinapsi.client.persistence;

import com.sinapsi.client.persistence.syncmodel.MacroChange;
import com.sinapsi.model.MacroInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Multi-platform implementation of DiffDBManager, which keeps all the changes
 * in an ArrayList in memory, instead of saving data to a persistent db
 * (used to make a diff between two macro DBs).
 */
public class MemoryDiffDBManager implements DiffDBManager {


    Map<Integer, MacroChange> changes = new HashMap<>();
    static int idCount = 0;

    @Override
    public void macroAdded(MacroInterface macro) throws InconsistentMacroChangeException {
        MacroChange mc = changes.get(macro.getId());
        if(mc == null)
            changes.put(macro.getId(), new MacroChange(idCount++, MacroChange.ChangeTypes.ADDED, macro.getId()));
        else{
            switch (mc.getChangeType()) {
                case ADDED:
                    // a macro with the same id has been added again: error
                    throw new InconsistentMacroChangeException("Tried to add a macro with the same id of an already existent (last change: added) macro.");

                case REMOVED:
                    // a macro previously removed has been added again: error
                    throw new InconsistentMacroChangeException("Tried to add a macro with the same id of a deleted macro.");

                case EDITED:
                    // a macro previously edited has benn added again: error
                    throw new InconsistentMacroChangeException("Tried to add a macro with the same id of an already existent (last change: edited) macro.");

            }
        }
    }

    @Override
    public void macroUpdated(MacroInterface macro) throws InconsistentMacroChangeException {
        MacroChange mc = changes.get(macro.getId());
        if(mc == null)
            changes.put(macro.getId(), new MacroChange(idCount++, MacroChange.ChangeTypes.EDITED, macro.getId()));
        else{
            switch (mc.getChangeType()) {
                case ADDED:
                    //a just added macro has been edited: leave it as added because the server doesn't know of its existence
                    break;
                case REMOVED:
                    // a macro previously removed has been edited? error
                    throw new InconsistentMacroChangeException("Tried to update a macro with the same id of a removed macro.");
                case EDITED:
                    //a just edited macro has been edited again: leave it as edited
                    break;
            }
        }
    }

    @Override
    public void macroRemoved(int macroId) throws InconsistentMacroChangeException {
        MacroChange mc = changes.get(macroId);
        if(mc == null)
            changes.put(macroId, new MacroChange(idCount++, MacroChange.ChangeTypes.EDITED, macroId));
        else{
            switch (mc.getChangeType()) {
                case ADDED:
                    // a macro previously added has been removed: delete the change entry
                    changes.remove(macroId);
                    break;
                case REMOVED:
                    throw new InconsistentMacroChangeException("Tried to delete a macro with the same id of an already deleted macro.");

                case EDITED:
                    // a macro previously edited has been deleted: change entry with delete
                    mc.setChangeType(MacroChange.ChangeTypes.REMOVED);
                    changes.put(macroId,mc);
                    break;
            }
        }
    }

    @Override
    public List<MacroChange> getAllChanges() {
        return new ArrayList<>(changes.values());
    }

    @Override
    public void clearDB() {
        changes.clear();
    }

    @Override
    public MacroChange getChangeForMacro(int id) {
        return changes.get(id);
    }

    @Override
    public int getMinMacroId() {
        Set<Integer> ids = changes.keySet();
        int min = 0;
        for(Integer i:ids){
            if(i<min) min = i;
        }
        return min;
    }
}
