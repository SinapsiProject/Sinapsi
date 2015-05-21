package com.sinapsi.client.web;

import android.provider.SyncStateContract;
import android.util.Base64;

import com.bgp.codec.DecodingMethod;
import com.bgp.codec.EncodingMethod;
import com.bgp.encryption.Encrypt;
import com.bgp.generator.KeyGenerator;
import com.bgp.keymanager.PublicKeyManager;
import com.bgp.keymanager.SessionKeyManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sinapsi.android.Lol;
import com.sinapsi.client.AppConsts;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.User;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;

import javax.crypto.SecretKey;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedOutput;

/**
 * WebService draft class.
 * This is now a class containing some example functions
 * to understand how RetroFit initializes a RetrofitInterface
 * variable.
 */
public class RetrofitWebServiceFacade implements SinapsiWebServiceFacade, BGPKeysProvider {

    //local keys
    private PublicKey publicKey;
    private PrivateKey privateKey;

    // keys generated by server
    private SecretKey serverSessionKey;
    private PublicKey serverPublicKey;

    private RetrofitInterface cryptedRetrofit;
    private RetrofitInterface uncryptedRetrofit;
    private RetrofitInterface loginRetrofit;

    private EncodingMethod encodingMethod;
    private DecodingMethod decodingMethod;

    private OnlineStatusProvider onlineStatusProvider;
    /**
     * Default ctor
     *
     * @param retrofitLog
     * @param onlineStatusProvider
     */
    public RetrofitWebServiceFacade(RestAdapter.Log retrofitLog, OnlineStatusProvider onlineStatusProvider, EncodingMethod encodingMethod, DecodingMethod decodingMethod) {

        this.onlineStatusProvider = onlineStatusProvider;
        this.encodingMethod = encodingMethod;
        this.decodingMethod = decodingMethod;

        Gson gson = new GsonBuilder().create();

        final GsonConverter defaultGsonConverter = new GsonConverter(gson);
        final BGPGsonConverter cryptInOutGsonConverter = new BGPGsonConverter(gson, this, this.encodingMethod, this.decodingMethod);

        //This converter only decrypts data from server
        final GsonConverter loginGsonConverter = new BGPGsonConverter(gson, this, this.encodingMethod, this.decodingMethod){
            @Override
            public TypedOutput toBody(Object object) {
                return defaultGsonConverter.toBody(object);
            }
        };

        RestAdapter cryptedRestAdapter = new RestAdapter.Builder()
                .setEndpoint(AppConsts.SINAPSI_URL)
                .setConverter(cryptInOutGsonConverter)
                .setLog(retrofitLog)
                .build();

        RestAdapter uncryptedRestAdapter = new RestAdapter.Builder()
                .setEndpoint(AppConsts.SINAPSI_URL)
                .setConverter(defaultGsonConverter)
                .setLog(retrofitLog)
                .build();

        RestAdapter loginRestAdapter = new RestAdapter.Builder()
                .setEndpoint(AppConsts.SINAPSI_URL)
                .setConverter(loginGsonConverter)
                .setLog(retrofitLog)
                .build();

        if (AppConsts.DEBUG) {
            cryptedRestAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            uncryptedRestAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            loginRestAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        } else {
            cryptedRestAdapter.setLogLevel(RestAdapter.LogLevel.NONE);
            uncryptedRestAdapter.setLogLevel(RestAdapter.LogLevel.NONE);
            loginRestAdapter.setLogLevel(RestAdapter.LogLevel.NONE);
        }

        cryptedRetrofit = cryptedRestAdapter.create(RetrofitInterface.class);

        uncryptedRetrofit = uncryptedRestAdapter.create(RetrofitInterface.class);

        loginRetrofit = loginRestAdapter.create(RetrofitInterface.class);
    }

    /**
     * Ctor with default encoding/decoding methods
     *
     * @param retrofitLog
     * @param onlineStatusProvider
     */
    public RetrofitWebServiceFacade(RestAdapter.Log retrofitLog, OnlineStatusProvider onlineStatusProvider){
        this(retrofitLog, onlineStatusProvider, null, null);
        //using null as methods here is safe because will force bgp library to use
        //default apache common codec methods
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public SecretKey getServerSessionKey() {
        return serverSessionKey;
    }




    /**
     * Converts a generic WebServiceCallback to a retrofit's Callback
     *
     * @param wsCallback the WebServiceCallback
     * @param <T>        the type
     * @return a retrofit's Callback
     */
    private static <T> Callback<T> convertCallback(final WebServiceCallback<T> wsCallback) {
        return new Callback<T>() {
            @Override
            public void success(T t, Response response) {
                wsCallback.success(t, response);
            }

            @Override
            public void failure(RetrofitError error) {
                wsCallback.failure(error);
            }
        };
    }

    /**
     * Check to ensure the keys are not null. Throws a runtime exception
     */
    private void checkKeys() {
        if (publicKey == null || privateKey == null || serverPublicKey == null || serverSessionKey == null) //TODO: check
            throw new RuntimeException("Missing key. Did you log in?");
    }

    /**
     * Request login
     * @param email email of the user
     * @param keysCallback public key and session key recived from the server
     */
    @Override
    public void requestLogin(String email, final WebServiceCallback<HashMap.SimpleEntry<byte[], byte[]>> keysCallback) {
        if(!onlineStatusProvider.isOnline()) return;

        KeyGenerator kg = new KeyGenerator(1024, "RSA");
        final PrivateKey prk = kg.getPrivateKey();
        final PublicKey puk = kg.getPublicKey();

        try {
            uncryptedRetrofit.requestLogin(email,
                    PublicKeyManager.convertToByte(puk),
                    new Callback<HashMap.SimpleEntry<byte[], byte[]>>() {

                @Override
                public void success(HashMap.SimpleEntry<byte[], byte[]> keys, Response response) {
                    RetrofitWebServiceFacade.this.publicKey = puk;
                    RetrofitWebServiceFacade.this.privateKey = prk;

                    try {
                        RetrofitWebServiceFacade.this.serverPublicKey = PublicKeyManager.convertToKey(keys.getKey());
                        RetrofitWebServiceFacade.this.serverSessionKey = SessionKeyManager.convertToKey(keys.getValue());
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        e.printStackTrace();
                    }


                    keysCallback.success(keys, response);
                }

                @Override
                public void failure(RetrofitError error) {
                    keysCallback.failure(error);
                }
            });
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login(String email, String password, final WebServiceCallback<User> result) {
        checkKeys();

        if(!onlineStatusProvider.isOnline()) return;

        try {
            Encrypt encrypt = new Encrypt(getServerPublicKey());
            encrypt.setCustomEncoding(encodingMethod);
            SecretKey sk = encrypt.getEncryptedSessionKey();

            loginRetrofit.login(email,
                    new HashMap.SimpleEntry<byte[], String>(SessionKeyManager.convertToByte(sk), encrypt.encrypt(password)),
                    new Callback<User>() {

                @Override
                public void success(User user, Response response) {
                    result.success(user, response);
                }

                @Override
                public void failure(RetrofitError error) {
                    result.failure(error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void register(String email, String password, WebServiceCallback<User> result) {
        if(!onlineStatusProvider.isOnline()) return;
        uncryptedRetrofit.register(email, password, convertCallback(result));

    }

    @Override
    public void getAllDevicesByUser(UserInterface user, WebServiceCallback<List<DeviceInterface>> result) {
        checkKeys();
        if(!onlineStatusProvider.isOnline()) return;
        cryptedRetrofit.getAllDevicesByUser(user.getEmail(), convertCallback(result));
    }

    @Override
    public void registerDevice(UserInterface user,
                               String emailUser,
                               String deviceName,
                               String deviceModel,
                               String deviceType,
                               int deviceClientVersion,
                               WebServiceCallback<DeviceInterface> result) {
        checkKeys();
        if(!onlineStatusProvider.isOnline()) return;
        cryptedRetrofit.registerDevice(
                emailUser,
                deviceName,
                deviceModel,
                deviceType,
                deviceClientVersion,
                Integer.toString(user.getId()),
                convertCallback(result));
    }

    @Override
    public void getAvailableActions(DeviceInterface device, WebServiceCallback<List<MacroComponent>> result) {
        checkKeys();
        if(!onlineStatusProvider.isOnline()) return;
        cryptedRetrofit.getAvailableActions(
                device.getId(),
                convertCallback(result));
    }

    @Override
    public void setAvailableActions(DeviceInterface device, List<MacroComponent> actions, WebServiceCallback<String> result) {
        checkKeys();
        if(!onlineStatusProvider.isOnline()) return;
        cryptedRetrofit.setAvailableActions(
                device.getId(),
                actions,
                convertCallback(result));
    }

    @Override
    public void getAvailableTriggers(DeviceInterface device, WebServiceCallback<List<MacroComponent>> result) {
        checkKeys();
        if(!onlineStatusProvider.isOnline()) return;
        cryptedRetrofit.getAvailableTriggers(
                device.getId(),
                convertCallback(result));
    }

    @Override
    public void setAvailableTriggers(DeviceInterface device, List<MacroComponent> triggers, WebServiceCallback<String> result) {
        checkKeys();
        if(!onlineStatusProvider.isOnline()) return;
        cryptedRetrofit.setAvailableTriggers(
                device.getId(),
                triggers,
                convertCallback(result));
    }

    @Override
    public void continueMacroOnDevice(DeviceInterface device, RemoteExecutionDescriptor red, WebServiceCallback<String> result) {
        //TODO: define this method
    }
}
