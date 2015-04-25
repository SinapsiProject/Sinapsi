/**
 * 
 */
package com.sinapsi.model.impl;

import com.sinapsi.model.ActionInterface;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.FactoryModelInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;

/**
 * Implementation of the default factory model interface
 *
 */
public class FactoryModel implements FactoryModelInterface {

	/* (non-Javadoc)
	 * @see com.sinapsi.model.FactoryModelInterface#newUser(int, java.lang.String, java.lang.String)
	 */
	@Override
	public UserInterface newUser(int id, String email, String password) {
		return new User(id, email, password);
	}

	/* (non-Javadoc)
	 * @see com.sinapsi.model.FactoryModelInterface#newDevice(int, java.lang.String, java.lang.String, java.lang.String, com.sinapsi.model.UserInterface, int)
	 */
	@Override
	public DeviceInterface newDevice(int id, String name, String model, String type, UserInterface user, int clientVersion) {
		return new Device(id, name, model, type, clientVersion, user);
	}

	/* (non-Javadoc)
	 * @see com.sinapsi.model.FactoryModelInterface#newMacro(java.lang.String, int)
	 */
	@Override
	public MacroInterface newMacro(String name, int id) {
		return new Macro(id, name);
	}

	/* (non-Javadoc)
	 * @see com.sinapsi.model.FactoryModelInterface#newActionAbstraction(int, int, java.lang.String)
	 */
	@Override
	public ActionInterface newActionAbstraction(int id, int minVersion, String name) {
		return new ActionAbstraction(id, minVersion, name);
	}

}
