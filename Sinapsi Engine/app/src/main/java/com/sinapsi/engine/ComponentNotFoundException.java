package com.sinapsi.engine;

import com.sinapsi.model.MacroComponent;

/**
 * Created by Giuseppe on 21/04/15.
 */
public class ComponentNotFoundException extends RuntimeException {
    public ComponentNotFoundException(String componentName, MacroComponent.ComponentTypes type){
        super(type.toString() + " component of name: '"+componentName+"' not found");
    }
}
