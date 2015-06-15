package com.sinapsi.android.persistence;

import android.content.Context;

import com.sinapsi.android.background.SinapsiBackgroundService;
import com.sinapsi.client.persistence.DiffDBManager;
import com.sinapsi.model.MacroInterface;

/**
 * DiffDBManager implementation for android
 */
public class AndroidDiffDBManager implements DiffDBManager {

    Context context;
    String dbname;

    public AndroidDiffDBManager(Context context, String dbname) {
        this.context = context;
        this.dbname = dbname;
    }

    @Override
    public void macroAdded(MacroInterface macro) {

    }

    @Override
    public void macroUpdated(MacroInterface macro) {

    }

    @Override
    public void macroRemoved(int id) {

    }

    @Override
    public void clearDB() {

    }
}
