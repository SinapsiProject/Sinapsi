package com.sinapsi.engine;

import com.sinapsi.model.Action;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: doku
 */
public class  ComponentLoader {

    private Class<? extends MacroComponent>[] classes;

    private List<Class<? extends MacroComponent>> errorClasses = new ArrayList<>();
    private Map<String, Class<? extends Trigger>> triggerClasses = new HashMap<>();
    private Map<String, Class<? extends Action>> actionClasses = new HashMap<>();

    /**
     * Creates a new component loader with the specified classes
     * @param classes the class array.
     */
    public ComponentLoader(Class<? extends MacroComponent>[] classes){
        this.classes = classes;
    }

    public boolean loadClasses(){
        boolean allOk = true;
        for(Class<? extends MacroComponent> c: classes){
            MacroComponent mc = null;
            try {
                mc = c.newInstance();  //TODO: default ctor in components
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (mc == null){
                errorClasses.add(c);
                allOk = false;
            } else {
                switch (mc.getComponentType()){
                    case TRIGGER:
                        triggerClasses.put(mc.getName(), (Class<? extends Trigger>)c);
                        break;
                    case ACTION:
                        actionClasses.put(mc.getName(), (Class<? extends Action>)c);
                        break;
                }
            }

        }

        return allOk;
    }

    public List<Class<? extends MacroComponent>> getErrorClasses() {
        return errorClasses;
    }

    public Map<String, Class<? extends Trigger>> getTriggerClasses() {
        return triggerClasses;
    }

    public Map<String, Class<? extends Action>> getActionClasses() {
        return actionClasses;
    }

    public List<String> getTriggerKeys(){
        return new ArrayList<>(triggerClasses.keySet());
    }

    public List<String> getActionKeys(){
        return new ArrayList<>(actionClasses.keySet());
    }

    public MacroComponent newComponentInstance(MacroComponent.ComponentTypes type, String key){
        try {
            switch (type) {
                case TRIGGER:
                    return (MacroComponent) triggerClasses.get(key).newInstance();
                case ACTION:
                    return (MacroComponent) actionClasses.get(key).newInstance();
            }
        }catch(InstantiationException | IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }
}
