package com.sinapsi.android;

import android.util.Log;

import com.sinapsi.client.AppConsts;

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
        if(AppConsts.DEBUG_LOGS) Log.d("DEBUGLOL", message);
    }

    /**
     * Prints a message in the log
     * @param tag the tag
     * @param message the message
     */
    public static void d(String tag, String message){
        if(AppConsts.DEBUG_LOGS) Log.d(tag, message);
    }

    /**
     * Prints a message in the log
     * @param clazz the class which name is used as tag
     * @param message the message
     */
    public static void d(Class clazz, String message){
        if(AppConsts.DEBUG_LOGS)Log.d(getTag(clazz), message);
    }

    /**
     * Prints a message in the log
     * @param o the object which class name is used as tag
     * @param message the message
     */
    public static void d(Object o, String message){
        if(AppConsts.DEBUG_LOGS)Lol.d(getTag(o),message);
    }

    public static String getTag(Object o){
        return getTag(o.getClass());
    }

    public static String getTag(Class clazz){
        return clazz.getSimpleName();
    }

    public static void printNullity(String tag, String name, Object o){
        d(tag, name + " is " + ((o==null)?"null":"not null"));
    }

    public static void printNullity(Object caller, String name, Object o){
        printNullity(getTag(caller), name, o);
    }

}