package com.sinapsi.engine;

import com.sinapsi.model.MacroComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Component loader class. Used by ComponentFactory to load classes from a list and
 * to instantiate at runtime specified components.
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
                mc = c.newInstance();
            } catch (Exception e) {
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

    /**
     * Returns a list of classes which loading was unsuccessful.
     *
     * @return the error class list
     */
    public List<Class<? extends MacroComponent>> getErrorClasses() {
        return errorClasses;
    }

    /**
     * Returns a map of valid Trigger classes
     *
     * @return the trigger class map
     */
    public Map<String, Class<? extends Trigger>> getTriggerClasses() {
        return triggerClasses;
    }

    /**
     * Returns a map of valid Action classes
     *
     * @return the action class map
     */
    public Map<String, Class<? extends Action>> getActionClasses() {
        return actionClasses;
    }

    /**
     * Returns a list of valid trigger names
     *
     * @return the list of names
     */
    public List<String> getTriggerKeys(){
        return new ArrayList<>(triggerClasses.keySet());
    }

    /**
     * Returns a list of valid action names
     *
     * @return the list of names
     */
    public List<String> getActionKeys(){
        return new ArrayList<>(actionClasses.keySet());
    }

    /**
     * Creates a new component instance of the specified type, with the specified name (key)
     * @param type the component type
     * @param key the component key/name
     * @return a new Component instance
     */
    public MacroComponent newComponentInstance(MacroComponent.ComponentTypes type, String key){
        try {
            switch (type) {
                case TRIGGER: {                  
                    return triggerClasses.get(key).newInstance();
                }
                case ACTION:
                    return actionClasses.get(key).newInstance();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
