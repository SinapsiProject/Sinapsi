package com.sinapsi.android.persistence;

import android.content.Context;

import com.sinapsi.client.persistence.DiffDBManager;
import com.sinapsi.model.MacroInterface;

/**
 * DiffDBManager implementation for android
 */
public class AndroidDiffDBManager implements DiffDBManager {

    public static final int DIFF_DB_VERSION = 1;

    public static final String TABLE_CHANGES = "macro";

    public static final String COL_CHANGE_ID = "id";
    public static final String COL_CHANGE_TYPE = "type";
    public static final String COL_CHANGE_MACRO_ID = "macro_id";

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
