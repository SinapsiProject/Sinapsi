package com.sinapsi.model;

/**
 * Trigger interface, this interface is not the real representation of Trigger, but gives support to database manager
 *
 */
public interface TriggerInterface {
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
