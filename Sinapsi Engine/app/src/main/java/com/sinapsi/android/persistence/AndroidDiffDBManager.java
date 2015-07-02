package com.sinapsi.android.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sinapsi.client.persistence.DiffDBManager;
import com.sinapsi.client.persistence.InconsistentMacroChangeException;
import com.sinapsi.client.persistence.syncmodel.MacroChange;
import com.sinapsi.model.MacroInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * DiffDBManager implementation for android
 */
public class AndroidDiffDBManager implements DiffDBManager {

    public static final int DIFF_DB_VERSION = 2;

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
            COL_CHANGE_MACRO_ID + " INTEGER NOT NULL UNIQUE, " +
            COL_CHANGE_TYPE + " TEXT" +
            ");";


    Context context;
    String dbname;
    public AndroidDiffDBOpenHelper diffDBOpenHelper;

    public AndroidDiffDBManager(Context context, String dbname) {
        this.context = context;
        this.dbname = dbname;
        diffDBOpenHelper = new AndroidDiffDBOpenHelper(context, dbname, null, DIFF_DB_VERSION);
    }


    @Override
    public void macroAdded(MacroInterface macro) throws InconsistentMacroChangeException {
        SQLiteDatabase db = diffDBOpenHelper.getWritableDatabase();

        Cursor checkCursor = db.rawQuery("SELECT * FROM " + TABLE_CHANGES +
                " WHERE " + COL_CHANGE_MACRO_ID + " = ?", new String[]{"" + macro.getId()});

        if(checkCursor == null || checkCursor.getCount() == 0){
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
        } else {
            checkCursor.moveToFirst();
            MacroChange mc = cursorToMacroChange(checkCursor);
            switch (mc.getChangeType()){
                case ADDED:
                    // a macro with the same id has been added again: error
                    throw new InconsistentMacroChangeException("Tried to add a macro with the same id of an already existent (last change: added) macro.");

                case REMOVED:
                    // a macro previously removed has been added again: error
                    throw new InconsistentMacroChangeException("Tried to add a macro with the same id of a deleted macro.");

                case EDITED:
                    // a macro previously edited has benn added again: error
                    throw new InconsistentMacroChangeException("Tried to add a macro with the same id of an already existent (last change: edited) macro.");
            }
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
    public void macroUpdated(MacroInterface macro) throws InconsistentMacroChangeException {
        SQLiteDatabase db = diffDBOpenHelper.getWritableDatabase();

        Cursor checkCursor = db.rawQuery("SELECT * FROM " + TABLE_CHANGES +
                " WHERE " + COL_CHANGE_MACRO_ID + " = ?", new String[]{"" + macro.getId()});

        if(checkCursor == null || checkCursor.getCount() == 0){
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
        } else {
            checkCursor.moveToFirst();
            MacroChange mc = cursorToMacroChange(checkCursor);
            switch (mc.getChangeType()){
                case ADDED:
                    //a just added macro has been edited: leave it as added because the server doesn't know of its existence
                    break;
                case REMOVED:
                    // a macro previously removed has been edited? error
                    throw new InconsistentMacroChangeException("Tried to update a macro with the same id of a removed macro.");
                case EDITED:
                    //a just edited macro has been edited again: leave it as edited
                    break;
            }
        }


        db.close();
    }

    @Override
    public void macroRemoved(int id) throws InconsistentMacroChangeException {
        SQLiteDatabase db = diffDBOpenHelper.getWritableDatabase();

        Cursor checkCursor = db.rawQuery("SELECT * FROM " + TABLE_CHANGES +
                " WHERE " + COL_CHANGE_MACRO_ID + " = ?", new String[]{"" + id});

        if(checkCursor == null || checkCursor.getCount() == 0){
            long rowid = db.insert(
                    TABLE_CHANGES,
                    null,
                    changeToContentValues(
                            new MacroChange(
                                    MacroChange.ChangeTypes.REMOVED, id)));
            if(rowid == -1){
                diffDBOpenHelper.close();
                throw new RuntimeException("An error occured while inserting a macro change in the diff db: "+dbname);
            }
        } else {
            checkCursor.moveToFirst();
            MacroChange mc = cursorToMacroChange(checkCursor);
            switch (mc.getChangeType()){
                case ADDED:
                    // a macro previously added has been removed: delete the change entry
                    checkCursor.close();
                    removeChange(id, db);
                    break;
                case REMOVED:
                    throw new InconsistentMacroChangeException("Tried to delete a macro with the same id of an already deleted macro.");

                case EDITED:
                    // a macro previously edited has been deleted: change entry with delete
                    checkCursor.close();
                    mc.setChangeType(MacroChange.ChangeTypes.REMOVED);
                    updateChange(mc, db);
                    break;
            }
        }


        db.close();
    }

    @Override
    public List<MacroChange> getAllChanges() {
        List<MacroChange> result = new ArrayList<>();
        Cursor c = diffDBOpenHelper.getWritableDatabase().query(TABLE_CHANGES, ALL_COLUMNS_CHANGES, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            result.add(cursorToMacroChange(c));
            c.moveToNext();
        }
        c.close();
        diffDBOpenHelper.close();
        return result;
    }

    @Override
    public void clearDB() {
        diffDBOpenHelper.getWritableDatabase().delete(TABLE_CHANGES, null, null);
        diffDBOpenHelper.close();
    }

    @Override
    public MacroChange getChangeForMacro(int id) {
        MacroChange result = null;
        Cursor c = diffDBOpenHelper.getWritableDatabase().query(
                TABLE_CHANGES,
                ALL_COLUMNS_CHANGES,
                COL_CHANGE_MACRO_ID+" = ?",
                new String[]{""+id},
                null,
                null,
                null);
        if(c == null || c.getCount() == 0) return null;
        c.moveToFirst();
        result = cursorToMacroChange(c);
        c.close();
        diffDBOpenHelper.close();
        return result;
    }

    @Override
    public int getMinMacroId() {
        SQLiteDatabase db = diffDBOpenHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT MIN(" + COL_CHANGE_MACRO_ID + ") FROM " + TABLE_CHANGES, null);
        if(c == null || c.getCount() == 0)
            return 0;
        c.moveToFirst();
        if(c.isNull(0))
            return 0;
        int min = c.getInt(0);
        c.close();
        db.close();
        diffDBOpenHelper.close();
        return Math.min(min, 0);
    }

    private void removeChange(int macroId, SQLiteDatabase db){
        db.delete(TABLE_CHANGES, COL_CHANGE_MACRO_ID + " = ?", new String[]{"" + macroId});
    }

    private void updateChange(MacroChange change, SQLiteDatabase db){
        db.update(TABLE_CHANGES, changeToContentValues(change), COL_CHANGE_MACRO_ID + " = ?",new String[]{""+change.getId()});
    }
}
