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
 * Used to serialize/deserialize DeviceInterface objects with GSON
 */
public class DeviceInterfaceTypeAdapter extends TypeAdapter<DeviceInterface> {

    private static final FactoryModel fm = new FactoryModel();

    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String DEVICE_SINAPSI_VERSION = "DEVICE_SINAPSI_VERSION";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String DEVICE_TYPE = "DEVICE_TYPE";
    public static final String DEVICE_MODEL = "DEVICE_MODEL";
    public static final String DEVICE_USER = "DEVICE_USER";



    @Override
    public void write(JsonWriter o, DeviceInterface d) throws IOException {
        if(d == null){
            o.nullValue();
            return;
        }
        o.beginObject();

        o.name(DEVICE_ID).value(d.getId());
        o.name(DEVICE_NAME).value(d.getName());
        o.name(DEVICE_TYPE).value(d.getType());
        o.name(DEVICE_MODEL).value(d.getModel());
        o.name(DEVICE_SINAPSI_VERSION).value(d.getVersion());

        o.name(DEVICE_USER);
        new UserInterfaceTypeAdapter().write(o, d.getUser());

        o.endObject();
    }

    @Override
    public DeviceInterface read(JsonReader i) throws IOException {
        if(i.peek() == JsonToken.NULL)
            return null;

        int id = -1;
        String name = null;
        String model = null;
        String type = null;
        UserInterface user = null;
        int clientVersion = -1;

        i.beginObject();
        while (i.hasNext()){
            String n = i.nextName();
            if(n.equals(DEVICE_ID))
                id = i.nextInt();

            if(n.equals(DEVICE_NAME))
                name = i.nextString();

            if(n.equals(DEVICE_MODEL))
                model = i.nextString();

            if(n.equals(DEVICE_TYPE))
                type = i.nextString();

            if(n.equals(DEVICE_SINAPSI_VERSION))
                clientVersion = i.nextInt();

            if(n.equals(DEVICE_USER))
                user = new UserInterfaceTypeAdapter().read(i);
        }
        i.endObject();

        return fm.newDevice(id, name, model, type, user, clientVersion);
    }
}
