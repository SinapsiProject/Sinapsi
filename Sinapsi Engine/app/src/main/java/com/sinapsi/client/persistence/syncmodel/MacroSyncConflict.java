package com.sinapsi.client.persistence.syncmodel;

import com.sinapsi.model.MacroInterface;

import java.util.List;

/**
 * Utility model class representing a conflict detected
 * during sync between the macro local db and the macros
 * in the remote server.
 */
public class MacroSyncConflict {
    private MacroInterface serverMacro;
    private MacroInterface localMacro;
    private List<MacroChange> serverChanges;
    private List<MacroChange> clientChanges;

    public MacroSyncConflict(MacroInterface serverMacro,
                             MacroInterface localMacro,
                             List<MacroChange> serverChanges,
                             List<MacroChange> clientChanges) {
        this.serverMacro = serverMacro;
        this.localMacro = localMacro;
        this.serverChanges = serverChanges;
        this.clientChanges = clientChanges;
    }

    public MacroInterface getServerMacro() {
        return serverMacro;
    }

    public MacroInterface getLocalMacro() {
        return localMacro;
    }

    public List<MacroChange> getServerChanges() {
        return serverChanges;
    }

    public List<MacroChange> getClientChanges() {
        return clientChanges;
    }
}
