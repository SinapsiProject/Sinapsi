package com.sinapsi.client.persistence;

import com.sinapsi.model.MacroInterface;

import java.util.Date;
import java.util.List;

/**
 * LocalDBFacade interface. This interface contains a collection
 * of methods useful to interact with persistent model data.
 */
public interface LocalDBManager {
    public boolean addOrUpdateMacro(MacroInterface macro);
    public List<MacroInterface> getAllMacros();
    public void removeMacro(int id);
    public void clearDB();
}
