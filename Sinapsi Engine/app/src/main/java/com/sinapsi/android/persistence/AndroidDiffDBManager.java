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
    public static final String COL_CHANGE_MACRO_ID = "macro_id";
    public static final String COL_CHANGE_TYPE = "type";

    public static final String[] ALL_COLUMNS_CHANGES = new String[]{
            COL_CHANGE_ID,
            COL_CHANGE_MACRO_ID,
            COL_CHANGE_TYPE
    };

    public static final String SQL_STATEMENT_CREATE_TABLE_CHANGES = "" +
            "CREATE TABLE " + TABLE_CHANGES + "(" +
            COL_CHANGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_CHANGE_MACRO_ID + " INTEGER, " +
            COL_CHANGE_TYPE + " TEXT" +
            ");";


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
