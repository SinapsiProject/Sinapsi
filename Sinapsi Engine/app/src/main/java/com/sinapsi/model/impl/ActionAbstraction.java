package com.sinapsi.model.impl;

import com.sinapsi.model.ActionInterface;

/**
 * Abstraction of Action, this class is not the real representation of Action object, but gives support to database manager
 * 
 */
public class ActionAbstraction implements ActionInterface {
	private int id;
	private int minVersion;
	private String name;
	
	/**
	 * Default ctor
	 */
	public ActionAbstraction(int ID, int minVer, String n) {
		id = ID;
		minVersion = minVer;
		name = n;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public int minVersion() {
		return minVersion;
	}

	@Override
	public String getName() {
		return name;
	}

}
