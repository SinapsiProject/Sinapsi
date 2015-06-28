package com.sinapsi.android.view.editor;

import android.content.Context;
import android.content.res.Resources;

/**
 * This class is used to handle localized names and descriptions
 * of components and parameters.
 */
public class EditorStringManager {

    private Context context;

    public EditorStringManager(Context context) {
        this.context = context;
    }

    public String getComponentName(String componentName){
        try {
            Resources r = context.getResources();
            return r.getString(r.getIdentifier(componentName+"_comp", "string", context.getPackageName()));
        }catch (Throwable t){
            t.printStackTrace();
            return componentName;
        }
    }

    public String getComponentDescription(String componentName){
        try {
            Resources r = context.getResources();
            return r.getString(r.getIdentifier(componentName+"_comp_desc", "string", context.getPackageName()));
        }catch (Throwable t){
            t.printStackTrace();
            return componentName;
        }
    }

    public String getParameterName(String parameterName){
        try {
            Resources r = context.getResources();
            return r.getString(r.getIdentifier(parameterName+"_param", "string", context.getPackageName()));
        }catch (Throwable t){
            t.printStackTrace();
            return parameterName;
        }
    }

    public String getParameterDescription(String parameterName){
        try {
            Resources r = context.getResources();
            return r.getString(r.getIdentifier(parameterName+"_param_desc", "string", context.getPackageName()));
        }catch (Throwable t){
            t.printStackTrace();
            return parameterName;
        }
    }

}
