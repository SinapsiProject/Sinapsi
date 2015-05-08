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
                ""
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

    @Override
    public DeviceInterface getSavedDevice() {
        int id = preferences.getInt("device_id", -1);
        String name = preferences.getString("device_name", null);
        String model = preferences.getString("device_model", null);
        String type = preferences.getString("device_type", null);
        int clVers = preferences.getInt("client_version", -1); //TODO: should this be loaded by appconsts instead?
        if(id == -1 || name == null || model == null || type == null || clVers == -1) return null;
        return fM.newDevice(id, name, model, type, getSavedUser(), clVers);
    }

    @Override
    public void saveDevice(DeviceInterface d) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("device_id", d.getId())
                .putString("device_name", d.getName())
                .putString("device_model", d.getModel())
                .putString("device_type", d.getType())
                .putInt("client_version", d.getVersion());
        editor.apply();
        saveUser(d.getUser());
    }
}
