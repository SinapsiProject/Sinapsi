package com.sinapsi.android.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Open Helper for android SQLite diff db.
 */
public class AndroidDiffDBOpenHelper extends SQLiteOpenHelper {
    public AndroidDiffDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AndroidDiffDBManager.TABLE_CHANGES);
        createTables(db);
    }


    public void createTables(SQLiteDatabase db){
        db.execSQL(AndroidDiffDBManager.SQL_STATEMENT_CREATE_TABLE_CHANGES);
    }
}
