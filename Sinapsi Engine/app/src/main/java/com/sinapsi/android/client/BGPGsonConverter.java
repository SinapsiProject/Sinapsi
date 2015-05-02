package com.sinapsi.android.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;

import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Custom Gson Converter extension class to support
 * BGP encryption/decryption.
 */
public class BGPGsonConverter extends GsonConverter {

    //This is also here because in the base class gson is not protected but private
    protected Gson myGson;

    public BGPGsonConverter(Gson gson) {
        super(gson, "UTF-8");
        this.myGson = gson;
    }

    @Override
    public Object fromBody(final TypedInput body, Type type) throws ConversionException {

        HashMap.SimpleEntry<String,String> cryptedPair = null;
        InputStreamReader inStrReader = null;
        try {
            //gets a new InputStreamReader
            inStrReader = new InputStreamReader(body.in(), "UTF-8");
            //converts the string from json to HashMap.SimpleEntry<SessionKey,String>
            cryptedPair =  myGson.fromJson(
                    inStrReader,
                    new TypeToken<HashMap.SimpleEntry<String,String>>(){}.getType());
            
            //
            //TODO: decrypt str from the crypted pair using bgp library
            String uncryptedStr = cryptedPair.getValue();
            //
            //

            final InputStream is = new ByteArrayInputStream(uncryptedStr.getBytes());

            TypedInput myBody = new TypedInput() {
                @Override
                public String mimeType() {
                    return body.mimeType();
                }

                @Override
                public long length() {
                    return body.length();
                }

                @Override
                public InputStream in() throws IOException {
                    return is;
                }
            };
            return super.fromBody(myBody, type);

        } catch (IOException e) {
            throw new ConversionException(e);
        } finally {
            if (inStrReader != null) {
                try {
                    inStrReader.close();
                } catch (IOException ignored) {
                    //
                }
            }
        }

    }

    @Override
    public TypedOutput toBody(Object object) {

        String message = myGson.toJson(object);

        //
        //TODO: encrypt message in a cryptedPair using bgp library
        HashMap.SimpleEntry<String,String> cryptedPair = new HashMap.SimpleEntry<>("key", message);
        //
        //

        return super.toBody(cryptedPair);
    }


}
