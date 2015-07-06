package com.sinapsi.client.persistence;

import com.sinapsi.model.MacroInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Multi-platform implementation of LocalDBManager, which keeps all the changes
 * in an ArrayList in memory, instead of saving data to a persistent db
 * (used to temporarily save pulled data before sync committing).
 */
public class MemoryLocalDBManager implements LocalDBManager{

    Map<Integer, MacroInterface> macros;

    public MemoryLocalDBManager(){
        macros = new HashMap<>();
    }

    /**
     * Creates this instance loading data from the db
     *
     * @param source the source db
     */
    public MemoryLocalDBManager(LocalDBManager source){
        macros = new HashMap<>();
        for(MacroInterface m: source.getAllMacros())
            addOrUpdateMacro(m);
    }


    @Override
    public boolean addOrUpdateMacro(MacroInterface macro) {
        MacroInterface check = macros.put(macro.getId(), macro);
        return check == null;
    }

    @Override
    public List<MacroInterface> getAllMacros() {
        return new ArrayList<>(macros.values());
    }

    @Override
    public void removeMacro(int id) {
        macros.remove(id);
    }

    @Override
    public void clearDB() {
        macros.clear();
    }

    @Override
    public int getMinMacroId() {
        int min = 0;
        for(MacroInterface m: macros.values())
            if(m.getId()<min) min = m.getId();
        return min;
    }

    @Override
    public boolean containsMacro(int id) {
        return macros.containsKey(id);
    }

    @Override
    public MacroInterface getMacroWithId(int id) {
        return macros.get(id);
    }

    @Override
    public void deleteMacrosWithNegativeId() {
        Integer[] indexes = macros.keySet().toArray(new Integer[macros.keySet().size()]);
        for(Integer i:indexes){
            if(i<0) macros.remove(i);
        }
    }

    public void saveToDb(LocalDBManager db, boolean clear){
        if(clear) db.clearDB();
        for (MacroInterface m :macros.values())
            db.addOrUpdateMacro(m);
    }
}
