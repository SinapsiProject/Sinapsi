package com.sinapsi.engine.model.impl;

import com.sinapsi.engine.model.MacroInterface;

/**
 * Created by Ayoub on 17/04/15.
 */
public class Macro implements MacroInterface {
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
