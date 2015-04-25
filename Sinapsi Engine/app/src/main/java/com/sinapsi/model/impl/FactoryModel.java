/**
 * 
 */
package com.sinapsi.model.impl;

import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.FactoryModelInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;

/**
 * Implementation of the default factory model interface
 *
 */
public class FactoryModel implements FactoryModelInterface {

	/**
	 * New user
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

}
