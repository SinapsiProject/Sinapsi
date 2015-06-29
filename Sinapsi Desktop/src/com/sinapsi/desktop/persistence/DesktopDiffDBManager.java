package com.sinapsi.desktop.persistence;

import java.util.List;

import com.sinapsi.client.persistence.DiffDBManager;
import com.sinapsi.client.persistence.syncmodel.MacroChange;
import com.sinapsi.model.MacroInterface;

public class DesktopDiffDBManager implements DiffDBManager {

	@Override
	public void macroAdded(MacroInterface macro) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void macroUpdated(MacroInterface macro) {
		// TODO SQLite
		
	}

	@Override
	public void macroRemoved(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearDB() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<MacroChange> getAllChanges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MacroChange getChangeForMacro(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMinMacroId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
