package com.sinapsi.android.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.sinapsi.client.persistence.UserSettingsFacade;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.FactoryModelInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.FactoryModel;

/**
 * UserSettingsFacade - Android implementation
 */
public class AndroidUserSettingsFacade implements UserSettingsFacade {

    public final String PREFS_NAME;
    private FactoryModelInterface fM = new FactoryModel();
    private SharedPreferences preferences;

    public AndroidUserSettingsFacade(String prefs_name, Context context) {
        this.PREFS_NAME = prefs_name;
        preferences = new ObscuredSharedPreferences(context, context.getSharedPreferences(prefs_name, 0));
    }

    @Override
    public UserInterface getSavedUser() {
        int id = preferences.getInt("user_id", -1);
        String email = preferences.getString("user_email", null);
        //String passw = preferences.getString("user_password", null);
        if(id == -1 || email == null /*|| passw == null*/) return null;
        return fM.newUser(
                id,
                email,
                "",
                false,
                "user"
        );
    }

    @Override
    public void saveUser(UserInterface u) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_email", u.getEmail());
        //editor.putString("user_password", u.getPassword());
        editor.putInt("user_id", u.getId());
        editor.apply();
    }
}
