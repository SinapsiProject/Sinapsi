package com.sinapsi.model;

/**
 * Empty system facade interface.
 * Used by components and engine for requirements check and
 * system resources management.
 *
 */
public interface SystemFacade {

    /**
     * Getter for system services.
     * @param key the String key to get the service
     *            from a data structure like a map.
     *            Tip: use String constants
     * @return an Object instance of the service. This
     *         then should be down-casted to the needed
     *         class by the service consumer.
     */
    public Object getSystemService(String key);

    /**
     * Setter for system services.
     * @param key the String key used to store the service
     *            in a data structure like a map.
     *            Tip: use String constants
     * @param o the Object instance of the service.
     */
    public void addSystemService(String key, Object o);

    /**
     * Method used to check the if the system meets a
     * specified requirement.
     * @param key the String key representing the requirement
     * @param required the minimum integer value required for
     *                 the specified requirement
     * @return false if the system did not set the requirement or
     *         if the 'required' parameter is greater than the
     *         value set by the system. true otherwise.
     */
    public boolean checkRequirement(String key, int required);

    /**
     * Method called by the client at startup to set all the
     * device's feature infos intended to be used in requirement
     * checks.
     * @param key the String key representing the requirement
     * @param value an integer value representing the requirement info
     *              (for example, 0 for missing and 1 for present,
     *              or in other cases, the version number of a
     *              particular API/library/system software).
     */
    public void setRequirementSpec(String key, int value);

}
