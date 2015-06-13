package com.sinapsi.android.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sinapsi.client.persistence.LocalDBManager;
import com.sinapsi.engine.Action;
import com.sinapsi.model.MacroInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * LocalDBManager implementation for Android
 */
public class AndroidLocalDBManager implements LocalDBManager {


    public static final int LOCAL_DB_VERSION = 1;

    public static final String TABLE_MACROS = "macro";
    public static final String TABLE_ACTION_LISTS = "action_list";

    public static final String COL_MACRO_ID = "id";
    public static final String COL_MACRO_NAME = "name";
    public static final String COL_MACRO_ICON_NAME = "icon_name";
    public static final String COL_MACRO_ICON_COLOR = "icon_color";
    public static final String COL_MACRO_VALID = "valid";
    public static final String COL_MACRO_FAILURE_POLICY = "failure_policy";
    public static final String COL_MACRO_ENABLED = "enabled";
    public static final String COL_MACRO_TRIGGER_DEVICE_ID = "trigger_device_id";
    public static final String COL_MACRO_TRIGGER_NAME = "trigger_name";
    public static final String COL_MACRO_TRIGGER_JSON = "trigger_json";

    public static final String[] ALL_COLUMNS_MACROS = new String[]{
            COL_MACRO_ID,
            COL_MACRO_NAME,
            COL_MACRO_ICON_NAME,
            COL_MACRO_ICON_COLOR,
            COL_MACRO_VALID,
            COL_MACRO_FAILURE_POLICY,
            COL_MACRO_ENABLED,
            COL_MACRO_TRIGGER_DEVICE_ID,
            COL_MACRO_TRIGGER_NAME,
            COL_MACRO_TRIGGER_JSON
    };

    public static final String COL_ACTIONLIST_MACRO_ID = "macro_id";
    public static final String COL_ACTIONLIST_ACTION_ORDER = "action_order";
    public static final String COL_ACTIONLIST_ACTION_DEVICE_ID = "action_device_id";
    public static final String COL_ACTIONLIST_ACTION_NAME = "action_name";
    public static final String COL_ACTIONLIST_ACTION_JSON = "action_json";

    public static final String[] ALL_COLUMNS_ACTIONLISTS = new String[]{
            COL_ACTIONLIST_MACRO_ID,
            COL_ACTIONLIST_ACTION_ORDER,
            COL_ACTIONLIST_ACTION_DEVICE_ID,
            COL_ACTIONLIST_ACTION_NAME,
            COL_ACTIONLIST_ACTION_JSON
    };

    public static final String SQL_STATEMENT_CREATE_TABLE_MACROS = "" +
            "CREATE TABLE " + TABLE_MACROS + " (" +
            "row_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_MACRO_ID + " INTEGER, " +
            COL_MACRO_NAME + " TEXT, " +
            COL_MACRO_ICON_NAME + " TEXT, " +
            COL_MACRO_ICON_COLOR + " VARCHAR(8), " +
            COL_MACRO_VALID + " INTEGER, " +
            COL_MACRO_ENABLED + " INTEGER, " +
            COL_MACRO_FAILURE_POLICY + " TEXT, " +
            COL_MACRO_TRIGGER_DEVICE_ID + " INTEGER, " +
            COL_MACRO_TRIGGER_NAME + " TEXT, " +
            COL_MACRO_TRIGGER_JSON + " TEXT"+
            ");";

    public static final String SQL_STATEMENT_CREATE_TABLE_ACTION_LISTS = "" +
            "CREATE TABLE " + TABLE_ACTION_LISTS + " (" +
            COL_ACTIONLIST_MACRO_ID + " INTEGER, " +
            COL_ACTIONLIST_ACTION_NAME + " TEXT, " +
            COL_ACTIONLIST_ACTION_ORDER + " INTEGER, " +
            COL_ACTIONLIST_ACTION_DEVICE_ID + " INTEGER, " +
            COL_ACTIONLIST_ACTION_JSON + " TEXT" +
            ");";


    private String dbname;
    private Context context;

    public AndroidLocalDBManager(Context c, String name){
        this.context = c;
    }

    public SQLiteOpenHelper localDBOpenHelper = new SQLiteOpenHelper(
            context,
            dbname,
            null,
            LOCAL_DB_VERSION) {
        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MACROS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION_LISTS);
            createTables(db);
        }

        public void createTables(SQLiteDatabase db){
            db.execSQL(SQL_STATEMENT_CREATE_TABLE_MACROS);
            db.execSQL(SQL_STATEMENT_CREATE_TABLE_ACTION_LISTS);
        }
    };

    @Override
    public void addOrUpdateMacro(MacroInterface macro) {

    }

    @Override
    public List<MacroInterface> getAllMacros() {
        List<MacroInterface> result = new ArrayList<>();

        Cursor c = localDBOpenHelper.getWritableDatabase().query(TABLE_MACROS,ALL_COLUMNS_MACROS,null,null,null,null,null);
        c.moveToFirst();

        while (!c.isAfterLast()){

            //TODO


        }
        c.close();
        localDBOpenHelper.close();
        return result;
    }

    @Override
    public void removeMacro(int id) {

    }

    private List<Action> getActionListForMacro(int macroid){
        List<Action> result = null;
        return result;
    }
}
