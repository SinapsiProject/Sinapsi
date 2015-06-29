package com.sinapsi.desktop.persistence;

import java.util.List;

import com.sinapsi.client.persistence.LocalDBManager;
import com.sinapsi.model.MacroInterface;

public class DesktopLocalDBManager implements LocalDBManager{

	@Override
	public boolean addOrUpdateMacro(MacroInterface macro) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<MacroInterface> getAllMacros() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeMacro(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearDB() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMinMacroId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean containsMacro(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MacroInterface getMacroWithId(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteMacrosWithNegativeId() {
		// TODO Auto-generated method stub
		
	}

}
