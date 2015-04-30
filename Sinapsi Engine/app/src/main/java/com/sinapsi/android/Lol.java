package com.sinapsi.android;

import android.util.Log;

/**
 * Created by Giuseppe on 04/03/15.
 * Debug-state driven Log proxy
 */
public class Lol {


    /**
     * Prints a message in the log
     * @param message the message
     */
    public static void d(String message){
        if(AppConsts.DEBUG) Log.d("DEBUGLOL", message);
    }

    /**
     * Prints a message in the log
     * @param tag the tag
     * @param message the message
     */
    public static void d(String tag, String message){
        if(AppConsts.DEBUG) Log.d(tag, message);
    }

    /**
     * Prints a message in the log
     * @param clazz the class which name is used as tag
     * @param message the message
     */
    public static void d(Class clazz, String message){
        if(AppConsts.DEBUG)Log.d(clazz.getSimpleName(), message);
    }

    /**
     * Prints a message in the log
     * @param o the object which class name is used as tag
     * @param message the message
     */
    public static void d(Object o, String message){
        if(AppConsts.DEBUG)Lol.d(o.getClass(),message);
    }

}