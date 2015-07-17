package com.sinapsi.client;

/**
 * Class containing a set of constant fields useful for development
 */
public class AppConsts {

    public static final boolean DEBUG_LOGS = true;
    public static final boolean DEBUG_TEST_MACROS = false;
    public static final boolean DEBUG_BYPASS_LOGIN = false;

    public static final boolean DEBUG_TEST_CREDENTIALS = true;
    public static final String DEBUG_TEST_EMAIL = "a@bf";
    public static final String DEBUG_TEST_PASSWORD = "1234567890";

    public static final boolean DEBUG_CLEAR_DB_ON_START = true;
    public static final boolean DEBUG_ENCRYPTED_RETROFIT = true;
    public static final boolean DEBUG_DISABLE_SYNC = false;

    public static final String SINAPSI_URL = "http://massolit.ns0.it:8181/sinapsi";
    public static final String SINAPSI_WS_URL = "wss://massolit.ns0.it:8887";

    public static final String PREFS_FILE_NAME = "SinapsiPrefs";

    public static final String DEFAULT_MACRO_COLOR = "#667a7f";
    public static final String DEFAULT_MACRO_ICON = "ic_macro_default";


    private AppConsts(){} //Don't instantiate plz
}
