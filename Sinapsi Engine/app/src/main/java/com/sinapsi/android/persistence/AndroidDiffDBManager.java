package com.sinapsi.android.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sinapsi.client.persistence.DiffDBManager;
import com.sinapsi.client.persistence.syncmodel.MacroChange;
import com.sinapsi.model.MacroInterface;

import java.util.ArrayList;
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
        SQLiteDatabase db = diffDBOpenHelper.getWritableDatabase();
        long rowid = db.insert(
                TABLE_CHANGES,
                null,
                changeToContentValues(
                        new MacroChange(
                                MacroChange.ChangeTypes.ADDED,
                                macro.getId())));
        if(rowid == -1){
            diffDBOpenHelper.close();
            throw new RuntimeException("An error occured while inserting a macro change in the diff db: "+dbname);
        }
        db.close();
    }

    private ContentValues changeToContentValues(MacroChange macroChange) {
        ContentValues cv = new ContentValues();

        cv.put(COL_CHANGE_MACRO_ID, macroChange.getMacroId());
        cv.put(COL_CHANGE_TYPE, macroChange.getChangeType().name());

        return cv;
    }

    private MacroChange cursorToMacroChange(Cursor c){
        int id = c.getInt(0);
        int macroid = c.getInt(1);
        String changetype = c.getString(2);

        return new MacroChange(id, MacroChange.ChangeTypes.valueOf(changetype), macroid);
    }

    @Override
    public void macroUpdated(MacroInterface macro) {
        SQLiteDatabase db = diffDBOpenHelper.getWritableDatabase();
        long rowid = db.insert(
                TABLE_CHANGES,
                null,
                changeToContentValues(
                        new MacroChange(
                                MacroChange.ChangeTypes.EDITED,
                                macro.getId())));
        if(rowid == -1){
            diffDBOpenHelper.close();
            throw new RuntimeException("An error occured while inserting a macro change in the diff db: "+dbname);
        }
        db.close();
    }

    @Override
    public void macroRemoved(int id) {
        SQLiteDatabase db = diffDBOpenHelper.getWritableDatabase();
        long rowid = db.insert(
                TABLE_CHANGES,
                null,
                changeToContentValues(
                        new MacroChange(
                                MacroChange.ChangeTypes.REMOVED,
                                id)));
        if(rowid == -1){
            diffDBOpenHelper.close();
            throw new RuntimeException("An error occured while inserting a macro change in the diff db: "+dbname);
        }
        db.close();
    }

    @Override
    public List<MacroChange> getAllChanges() {
        List<MacroChange> result = new ArrayList<>();
        Cursor c = diffDBOpenHelper.getWritableDatabase().query(TABLE_CHANGES,ALL_COLUMNS_CHANGES,null,null,null,null, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            result.add(cursorToMacroChange(c));
        }
        c.close();
        diffDBOpenHelper.close();
        return result;
    }

    @Override
    public void clearDB() {
        diffDBOpenHelper.getWritableDatabase().rawQuery("DELETE FROM " + TABLE_CHANGES, null);
        diffDBOpenHelper.close();
    }

    @Override
    public List<MacroChange> getChangesForMacro(int id) {
        List<MacroChange> result = new ArrayList<>();
        Cursor c = diffDBOpenHelper.getWritableDatabase().query(
                TABLE_CHANGES,
                ALL_COLUMNS_CHANGES,
                COL_CHANGE_MACRO_ID+" = ?",
                new String[]{""+id},
                null,
                null,
                null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            result.add(cursorToMacroChange(c));
        }
        c.close();
        diffDBOpenHelper.close();
        return result;
    }
}
