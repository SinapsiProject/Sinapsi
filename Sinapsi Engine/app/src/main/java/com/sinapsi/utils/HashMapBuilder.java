package com.sinapsi.utils;

import java.util.HashMap;

/**
 * Utility class for inline-style initialization of HashMaps.
 */
public class HashMapBuilder <K, V>{

    private HashMap<K,V> map = new HashMap<K,V>();

    /**
     * Puts a new pair (key, value) inside the temporary map.
     * @param key the key
     * @param value the value
     * @return the invocation object itself, to allow method
     *         chaining.
     */
    public HashMapBuilder put(K key, V value){
        map.put(key,value);
        return this;
    }

    /**
     * Returns the map just created via this builder.
     * @return the new HashMap
     */
    public HashMap<K, V> create(){
        return map;
    }
}
