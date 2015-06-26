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
    private MacroChange serverChange;
    private MacroChange clientChange;

    public MacroSyncConflict(MacroInterface serverMacro,
                             MacroInterface localMacro,
                             MacroChange serverChange,
                             MacroChange clientChange) {
        this.serverMacro = serverMacro;
        this.localMacro = localMacro;
        this.serverChange = serverChange;
        this.clientChange = clientChange;
    }

    public MacroInterface getServerMacro() {
        return serverMacro;
    }

    public MacroInterface getLocalMacro() {
        return localMacro;
    }

    public MacroChange getServerChanges() {
        return serverChange;
    }

    public MacroChange getClientChanges() {
        return clientChange;
    }
}
