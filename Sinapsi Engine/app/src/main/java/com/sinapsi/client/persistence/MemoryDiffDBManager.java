package com.sinapsi.client.persistence;

import com.sinapsi.client.persistence.syncmodel.MacroChange;
import com.sinapsi.model.MacroInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Multi-platform implementation of DiffDBManager, which keeps all the changes
 * in an ArrayList in memory, instead of saving data to a persistent db
 * (used to make a diff between two macro DBs).
 */
public class MemoryDiffDBManager implements DiffDBManager {

    List<MacroChange> changes = new ArrayList<>();
    static int idCount = 0;

    @Override
    public void macroAdded(MacroInterface macro) {
        changes.add(new MacroChange(idCount++, MacroChange.ChangeTypes.ADDED, macro.getId()));
    }

    @Override
    public void macroUpdated(MacroInterface macro) {
        changes.add(new MacroChange(idCount++, MacroChange.ChangeTypes.EDITED, macro.getId()));
    }

    @Override
    public void macroRemoved(int id) {
        changes.add(new MacroChange(idCount++, MacroChange.ChangeTypes.REMOVED, id));
    }

    @Override
    public List<MacroChange> getAllChanges() {
        return changes;
    }

    @Override
    public void clearDB() {
        changes.clear();
    }

    @Override
    public List<MacroChange> getChangesForMacro(int id) {
        List<MacroChange> result = new ArrayList<>();
        for(MacroChange mc:getAllChanges()){
            if(mc.getMacroId() == id) result.add(mc);
        }
        return result;
    }
}
