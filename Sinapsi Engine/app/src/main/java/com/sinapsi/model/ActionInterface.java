package com.sinapsi.model;

/**
 * Action interface, this interface is not the real representation of Action, but gives support to database manager
 *
 */
public interface ActionInterface {
	/**
	 * Return the id of the action
	 * @return
	 */
	public int getId();
	
	/**
	 * Return the min version of action
	 * @return
	 */
	public int minVersion();
	
	/**
	 * return the name of action
	 * @return
	 */
	public String getName();
}
