package com.sinapsi.model.impl;

import com.sinapsi.model.MacroComponent;

import java.util.HashMap;

/**
 * Abstraction of Trigger, this class is not the real representation of Trigger object, but gives support to database manager
 * 
 */
public class TriggerDescriptor extends ComunicationError implements MacroComponent {
	private int minVersion;
	private String name;
	
	/**
	 * Default ctor
	 */
	public TriggerDescriptor(int minVer, String n) {
		super();
		minVersion = minVer;
		name = n;
	}

	@Override
	public int getMinVersion() {
		return minVersion;
	}

	@Override
	public String getName() {
		return name;
	}


    @Override
    public ComponentTypes getComponentType() {
        return ComponentTypes.TRIGGER;
    }

    @Override
    public HashMap<String, Integer> getSystemRequirementKeys() {
        return null;
    }
}
