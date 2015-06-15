package com.sinapsi.client.persistence.syncmodel;

/**
 * Utility model class representing a change in the set
 * of macros.
 */
public class MacroChange implements Comparable<MacroChange>{



    public enum ChangeTypes{
        ADDED,
        REMOVED,
        EDITED
    }

    private int id = -1;
    private ChangeTypes changeType;
    private int macroId;


    public MacroChange(ChangeTypes changeType, int macroId) {
        this.changeType = changeType;
        this.macroId = macroId;
    }

    public MacroChange(int id, ChangeTypes changeType, int macroId) {
        this.id = id;
        this.changeType = changeType;
        this.macroId = macroId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChangeTypes getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeTypes changeType) {
        this.changeType = changeType;
    }

    public int getMacroId() {
        return macroId;
    }

    public void setMacroId(int macroId) {
        this.macroId = macroId;
    }

    @Override
    public int compareTo(MacroChange another) {
        return this.getId() - another.getId();
    }
}
