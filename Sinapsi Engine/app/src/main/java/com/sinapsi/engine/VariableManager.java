package com.sinapsi.engine;

import com.sinapsi.utils.Pair;

import java.util.HashMap;

/**
 * Simple class used to identify a scope and the variables in it.
 */
public class VariableManager {

    private HashMap<String, Pair<Types, Object>> map = new HashMap<>();

    /**
     * Puts a variable with the given name, type and value in the map.
     * @param strname the variable's name
     * @param type the variable's type
     * @param strvalue the string representation of the variable's value.
     */
    public void putVar(String strname, Types type, String strvalue) {
        switch (type){
            case STRING:
                map.put(strname, new Pair<Types, Object>(type, strvalue));
                break;
            case INT:
            {
                Integer x = Integer.parseInt(strvalue);
                map.put(strname, new Pair<Types, Object>(type, x));
            }
                break;
            case BOOLEAN:
            {
                Boolean x = Boolean.parseBoolean(strvalue);
                map.put(strname,  new Pair<Types, Object>(type, x));
            }
                break;
        }
    }

    /**
     * Gets the type of the variable with the specified name.
     * @param strName the name
     * @return the type if the name is valid, null otherwise
     */
    public Types getVarType(String strName){
        if(map.containsKey(strName))
            return map.get(strName).getFirst();
        else return null;
    }


    /**
     * Gets the value of the variable with the specified name.
     * @param strName the name
     * @return the value if the name is valid, null otherwise
     */
    public Object getVarValue(String strName){
        if(map.containsKey(strName))
            return map.get(strName).getSecond();
        else return null;
    }

    public String getStringRepresentationOfValue(String strName){
        if(!map.containsKey(strName)) return null;

        switch (getVarType(strName)){
            case STRING:
                return (String) getVarValue(strName);
            case INT:
                return ((Integer) getVarValue(strName)).toString();
            case BOOLEAN:
                return ((Boolean) getVarValue(strName)).toString();
        }

        return null;
    }

    public boolean containsVariable(String name){
        return map.containsKey(name);
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
