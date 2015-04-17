package com.sinapsi.engine.model.impl;

import com.sinapsi.engine.model.Action;
import com.sinapsi.engine.model.MacroInterface;
import com.sinapsi.engine.model.Trigger;
import java.util.List;

/**
 * Created by Ayoub on 17/04/15.
 */
public class Macro implements MacroInterface {
    private Trigger trigger;
    private List<Action> actions;
    private String name;
    private int id;

    /**
     * Return the name of the macro
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Return the id of the macro
     * @return
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Set a name for the macro
     * @param nm name
     */
    @Override
    public void setName(String nm) {
        name = nm;
    }
}
