package com.sinapsi.android.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sinapsi.android.Lol;

/**
 * Open Helper for android SQLite local db.
 */
public class AndroidLocalDBOpenHelper extends SQLiteOpenHelper{
    public AndroidLocalDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Lol.d(this, "ON CREATE CALLED");
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Lol.d("ON UPGRADE CALLED from " +oldVersion+" to "+newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + AndroidLocalDBManager.TABLE_MACROS);
        db.execSQL("DROP TABLE IF EXISTS " + AndroidLocalDBManager.TABLE_ACTION_LISTS);
        createTables(db);
    }

    public void createTables(SQLiteDatabase db){
        db.execSQL(AndroidLocalDBManager.SQL_STATEMENT_CREATE_TABLE_MACROS);
        db.execSQL(AndroidLocalDBManager.SQL_STATEMENT_CREATE_TABLE_ACTION_LISTS);
    }
}
