package com.sinapsi.webshared.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.FactoryModel;

import java.io.IOException;

/**
 * Used to serialize/deserialize UserInterface objects with GSON
 */
public class UserInterfaceTypeAdapter extends TypeAdapter<UserInterface>{

    private static final FactoryModel fm = new FactoryModel();

    public static final String USER_ID = "USER_ID";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_ACTIVATION = "USER_ACTIVATION";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String USER_ROLE = "USER_ROLE";


    @Override
    public void write(JsonWriter o, UserInterface u) throws IOException {
        if(u == null){
            o.nullValue();
            return;
        }

        o.beginObject();

        o.name(USER_ID).value(u.getId());
        o.name(USER_EMAIL).value(u.getEmail());
        o.name(USER_ACTIVATION).value(u.getActivation());
        o.name(USER_PASSWORD).value(u.getPassword());
        o.name(USER_ROLE).value(u.getRole());

        o.endObject();
    }

    @Override
    public UserInterface read(JsonReader i) throws IOException {
        if(i.peek() == JsonToken.NULL)
            return null;

        int id = -1;
        String email = null;
        String password = null;
        boolean active = false;
        String role = null;

        i.beginObject();
        while (i.hasNext()){
            String n = i.nextName();
            if(n.equals(USER_ID))
                id = i.nextInt();

            if(n.equals(USER_EMAIL))
                email = i.nextString();

            if(n.equals(USER_ACTIVATION))
                active = i.nextBoolean();

            if(n.equals(USER_PASSWORD))
                password = i.nextString();

            if(n.equals(USER_ROLE))
                role = i.nextString();
        }
        i.endObject();

        return fm.newUser(id, email, password, active, role);
    }
}
