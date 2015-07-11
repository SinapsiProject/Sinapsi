package com.sinapsi.utils;

import com.sinapsi.model.impl.CommunicationInfo;

/**
 * Class used to serialize a triplet of object across Sinapsi's network
 */
public class Triplet<T1,T2,T3> extends CommunicationInfo {
    T1 first;
    T2 second;
    T3 third;

    public Triplet(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    public T3 getThird() {
        return third;
    }
}