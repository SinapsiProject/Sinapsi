package com.sinapsi.client;

import com.bgp.decryption.Decrypt;
import com.bgp.encryption.Encrypt;
import com.bgp.generator.KeyGenerator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinapsi.android.Lol;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import javax.crypto.SecretKey;
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
    private BGPKeysProvider keysProvider;

    //TODO: handle exceptions

    /**
     * Default ctor
     * @param gson the gson object
     * @param keysProvider the key provider
     */
    public BGPGsonConverter(Gson gson, BGPKeysProvider keysProvider) {
        super(gson, "UTF-8");
        this.myGson = gson;
        this.keysProvider = keysProvider;
    }

    /// Converts from body to object
    @Override
    public Object fromBody(final TypedInput body, Type type) throws ConversionException {

        HashMap.SimpleEntry<SecretKey,String> cryptedPair = null;
        InputStreamReader inStrReader = null;
        try {
            //gets a new InputStreamReader
            inStrReader = new InputStreamReader(body.in(), "UTF-8");
            //converts the string from json to HashMap.SimpleEntry<SessionKey,String>
            cryptedPair =  myGson.fromJson(
                    inStrReader,
                    new TypeToken<HashMap.SimpleEntry<SecretKey,String>>(){}.getType());

            //decrypts the message
            Decrypt decrypter = new Decrypt(keysProvider.getPrivateKey(), cryptedPair.getKey());
            String uncryptedStr = decrypter.decrypt(cryptedPair.getValue());

            //calls super to convert to object
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
        } catch (Exception e) {
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

    /// converts from object to body
    @Override
    public TypedOutput toBody(Object object) {

        String message = myGson.toJson(object);

        try {
            Encrypt encrypter = new Encrypt(keysProvider.getPublicKey());
            String cryptedString = encrypter.encrypt(message);
            HashMap.SimpleEntry<SecretKey,String> cryptedPair = new HashMap.SimpleEntry<>(encrypter.getEncryptedSessionKey(), message);
            return super.toBody(cryptedPair);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }


}
