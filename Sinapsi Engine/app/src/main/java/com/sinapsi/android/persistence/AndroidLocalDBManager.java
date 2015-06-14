package com.sinapsi.android.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sinapsi.client.persistence.LocalDBManager;
import com.sinapsi.engine.Action;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.engine.Trigger;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.FactoryModel;

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

    private ComponentFactory componentFactory;
    private FactoryModel factoryModel = new FactoryModel();

    public AndroidLocalDBManager(Context c, String name, ComponentFactory cfactory){
        this.context = c;
        this.dbname = name;
        this.componentFactory = cfactory;
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

        SQLiteDatabase db = localDBOpenHelper.getWritableDatabase();
        Cursor c = db.query(TABLE_MACROS, ALL_COLUMNS_MACROS, null, null, null, null, null);
        c.moveToFirst();

        while (!c.isAfterLast()){

            int id = c.getInt(0);
            String name = c.getString(1);
            String iconName = c.getString(2);
            String iconColor = c.getString(3);
            boolean valid = c.getInt(4) != 0;
            String failurePolicy = c.getString(5);
            boolean enabled = c.getInt(6) != 0;

            int triggerDeviceId = c.getInt(7);
            String triggerName = c.getString(8);
            String triggerJSON = c.getString(9);

            MacroInterface m = factoryModel.newMacro(name, id);
            m.setIconName(iconName);
            m.setMacroColor(iconColor);
            m.setValid(valid);
            m.setExecutionFailurePolicy(failurePolicy);
            m.setEnabled(enabled);

            Trigger t = componentFactory.newTrigger(
                    triggerName,
                    triggerJSON,
                    m,
                    factoryModel.newDevice(
                            triggerDeviceId,
                            "",
                            "",
                            "",
                            null,
                            -1)); //TODO: check if this works with only id

            m.setTrigger(t);

            for(Action ac: getActionListForMacro(m.getId(), db)){
                m.addAction(ac);
            }

            result.add(m);
        }
        c.close();
        localDBOpenHelper.close();
        return result;
    }

    @Override
    public void removeMacro(int id) {

    }

    @Override
    public void clearDB() {
        localDBOpenHelper.getWritableDatabase().rawQuery("DELETE FROM "+TABLE_MACROS, null);
        localDBOpenHelper.getWritableDatabase().rawQuery("DELETE FROM "+TABLE_ACTION_LISTS, null);
    }

    private List<Action> getActionListForMacro(int macroid, SQLiteDatabase db){
        List<Action> result = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ACTION_LISTS +
                " WHERE " + COL_ACTIONLIST_MACRO_ID + " = ? " +
                " ORDER BY " + COL_ACTIONLIST_ACTION_ORDER, new String[]{""+macroid});

        c.moveToFirst();

        while(!c.isAfterLast()){
            String actionName = c.getString(c.getColumnIndexOrThrow(COL_ACTIONLIST_ACTION_NAME));
            int actionDeviceId = c.getInt(c.getColumnIndexOrThrow(COL_ACTIONLIST_ACTION_DEVICE_ID));
            String actionParams = c.getString(c.getColumnIndexOrThrow(COL_ACTIONLIST_ACTION_JSON));

            Action ac = componentFactory.newAction(
                    actionName,
                    actionParams,
                    factoryModel.newDevice(
                            actionDeviceId,
                            "",
                            "",
                            "",
                            null,
                            -1)); //TODO: check if this works with only id

            result.add(ac);
        }

        c.close();

        return result;
    }
}
