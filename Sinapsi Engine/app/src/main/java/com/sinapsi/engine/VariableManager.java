package com.sinapsi.engine;

import java.util.HashMap;

/**
 * Simple class used to identify a scope and the variables in it.
 */
public class VariableManager {

    private HashMap<String, HashMap.SimpleEntry<Types, Object>> map = new HashMap<>();

    /**
     * Puts a variable with the given name, type and value in the map.
     * @param strname the variable's name
     * @param type the variable's type
     * @param strvalue the string representation of the variable's value.
     */
    public void putVar(String strname, Types type, String strvalue) {
        switch (type){
            case STRING:
                map.put(strname, new HashMap.SimpleEntry<Types, Object>(type, strvalue));
                break;
            case INT:
            {
                Integer x = Integer.parseInt(strvalue);
                map.put(strname, new HashMap.SimpleEntry<Types, Object>(type, x));
            }
                break;
            case BOOLEAN:
            {
                Boolean x = Boolean.parseBoolean(strvalue);
                map.put(strname,  new HashMap.SimpleEntry<Types, Object>(type, x));
            }
                break;
        }
    }

    /**
     * Gets the type of the variable with the specified name.
     * @param strName the name
     * @return the type
     */
    public Types getVarType(String strName){
        return map.get(strName).getKey();
    }


    /**
     * Gets the value of the variable with the specified name.
     * @param strName the name
     * @return the value
     */
    public Object getVarValue(String strName){
        return map.get(strName).getValue();
    }

    public enum Types {
        STRING,
        INT,
        BOOLEAN
    }

    public enum Scopes{
        LOCAL,
        GLOBAL
    }


}
