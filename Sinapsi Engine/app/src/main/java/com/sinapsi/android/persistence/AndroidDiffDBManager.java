package com.sinapsi.android.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sinapsi.client.persistence.DiffDBManager;
import com.sinapsi.client.persistence.syncmodel.MacroChange;
import com.sinapsi.model.MacroInterface;

import java.util.List;

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

    public SQLiteOpenHelper diffDBOpenHelper = new SQLiteOpenHelper(
            context,
            dbname,
            null,
            DIFF_DB_VERSION
    ) {
        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANGES);
            createTables(db);
        }


        public void createTables(SQLiteDatabase db){
            db.execSQL(SQL_STATEMENT_CREATE_TABLE_CHANGES);
        }
    };

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
    public List<MacroChange> getAllChanges() {
        return null;
    }

    @Override
    public void clearDB() {

    }
}
