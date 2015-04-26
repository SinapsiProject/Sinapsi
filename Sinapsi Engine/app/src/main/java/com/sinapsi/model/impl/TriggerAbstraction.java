package com.sinapsi.model.impl;

import com.sinapsi.model.TriggerInterface;

/**
 * Abstraction of Trigger, this class is not the real representation of Trigger object, but gives support to database manager
 * 
 */
public class TriggerAbstraction extends ComunicationError implements TriggerInterface {
	private int id;
	private int minVersion;
	private String name;
	
	/**
	 * Default ctor
	 */
	public TriggerAbstraction(int ID, int minVer, String n) {
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
	public int minVersion() {
		return minVersion;
	}

	@Override
	public String getName() {
		return name;
	}
}
