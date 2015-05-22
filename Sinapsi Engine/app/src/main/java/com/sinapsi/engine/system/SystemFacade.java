package com.sinapsi.engine.system;

import com.sinapsi.model.MacroComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * SystemFacade class.
 * Used by components and engine for requirements check and
 * system resources management.
 *
 */
public class SystemFacade {


    private Map<String, Object> services = new HashMap<>();
    private Map<String, Integer> systemFeatures = new HashMap<>();

    /**
     * Getter for system services.
     * @param key the String key to get the service
     *            from a data structure like a map.
     *            Tip: use String constants
     * @return an Object instance of the service. This
     *         then should be down-casted to the needed
     *         class by the service consumer.
     */
    public Object getSystemService(String key){
        return services.get(key);
    }

    /**
     * Setter for system services.
     * @param key the String key used to store the service
     *            in a data structure like a map.
     *            Tip: use String constants
     * @param o the Object instance of the service.
     * @return the invocation object itself, to allow method
     *         chaining.
     */
    public SystemFacade addSystemService(String key, Object o){
        services.put(key, o);
        return this;
    }

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
    public boolean checkRequirement(String key, int required){
        Integer x = systemFeatures.get(key);
        return x != null && required >= x;
    }

    /**
     * Checks all requirements of a specified MacroComponent
     * @param component the component
     * @return true if all requirements are met by this system,
     *         false otherwise.
     */
    public boolean checkRequirements(MacroComponent component){
        HashMap<String, Integer> requirements = component.getSystemRequirementKeys();
        if(requirements==null) return true;
        for(String s :requirements.keySet()){
            if(!checkRequirement(s,requirements.get(s)))
                return false;
        }
        return true;
    }

    /**
     * Method called by the client at startup to set all the
     * device's feature infos intended to be used in requirement
     * checks.
     * @param key the String key representing the requirement
     * @param value an integer value representing the requirement info
     *              (for example, 0 for missing and 1 for present,
     *              or in other cases, the version number of a
     *              particular API/library/system software).
     * @return the invocation object itself, to allow method
     *         chaining.
     */
    public SystemFacade setRequirementSpec(String key, int value){
        systemFeatures.put(key,value);
        return this;
    }

    /**
     * Method called by the client at startup to set all the
     * device's feature infos intended to be used in requirement
     * checks. Calling this is equivalent to calling
     * setRequirementSpec(String, int) by passing 0 if value is
     * false and 1 if value is true.
     * @param key the String key representing the requirement
     * @param value a boolean value representing the feature
     *              availability on the device (true for present
     *              and false for missing):
     * @return the invocation object itself, to allow method
     *         chaining.
     */
    public SystemFacade setRequirementSpec(String key, boolean value){
        systemFeatures.put(key,value?1:0);
        return this;
    }


}
