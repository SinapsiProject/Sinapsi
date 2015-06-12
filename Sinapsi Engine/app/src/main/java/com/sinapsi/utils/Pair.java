package com.sinapsi.utils;

import com.sinapsi.model.impl.ComunicationInfo;

/**
 * Class used to serialize a pair of object across Sinapsi's network
 */
public class Pair<T1, T2> extends ComunicationInfo{

    private T1 first = null;
    private T2 second = null;

    public Pair(T1 first, T2 second){
        this.first = first;
        this.second = second;
    }

    public T1 getFirst(){
        return first;
    }

    public T2 getSecond(){
        return second;
    }

}
