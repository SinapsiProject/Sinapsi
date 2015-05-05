package com.sinapsi.model.impl;

import com.sinapsi.model.MacroComponent;

import java.util.HashMap;

/**
 * Abstraction of Action, this class is not the real representation of Action object, but gives support to database manager
 * 
 */
public class ActionAbstraction extends ComunicationError implements MacroComponent {
	private int id;
	private int minVersion;
	private String name;
	
	/**
	 * Default ctor
	 */
	public ActionAbstraction(int ID, int minVer, String n) {
		super();
		id = ID;
		minVersion = minVer;
		name = n;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getMinVersion() {
		return minVersion;
	}

    @Override
    public ComponentTypes getComponentType() {
        return ComponentTypes.ACTION;
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return null;
    }

    @Override
	public String getName() {
		return name;
	}
}
