package com.sinapsi.client.web;

import com.bgp.codec.DecodingMethod;
import com.bgp.codec.EncodingMethod;
import com.bgp.encryption.Encrypt;
import com.bgp.generator.KeyGenerator;
import com.bgp.keymanager.PublicKeyManager;
import com.bgp.keymanager.SessionKeyManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sinapsi.client.AppConsts;
import com.sinapsi.client.web.gson.DeviceInterfaceInstanceCreator;
import com.sinapsi.client.websocket.WSClient;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.model.impl.User;
import com.sinapsi.utils.Pair;
import com.sinapsi.wsproto.WebSocketEventHandler;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URISyntaxException;
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

    private FactoryModel factoryModel = new FactoryModel();

    //local keys
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SecretKey localUncryptedSessionKey;

    // keys generated by server
    private SecretKey serverSessionKey;
    private PublicKey serverPublicKey;

    private RetrofitInterface cryptedRetrofit;
    private RetrofitInterface uncryptedRetrofit;
    private RetrofitInterface loginRetrofit;

    private EncodingMethod encodingMethod;
    private DecodingMethod decodingMethod;


    private OnlineStatusProvider onlineStatusProvider;
    private WSClient wsClient = null;
    private WebSocketEventHandler webSocketEventHandler;
    private LoginStatusListener loginStatusListener;

    private UserInterface loggedUser = null;

    /**
     * Default ctor
     *
     * @param retrofitLog
     * @param onlineStatusProvider
     */
    public RetrofitWebServiceFacade(RestAdapter.Log retrofitLog,
                                    OnlineStatusProvider onlineStatusProvider,
                                    WebSocketEventHandler wsEventHandler,
                                    LoginStatusListener loginStatusListener,
                                    EncodingMethod encodingMethod,
                                    DecodingMethod decodingMethod) {

        this.webSocketEventHandler = wsEventHandler;

        this.onlineStatusProvider = onlineStatusProvider;
        this.loginStatusListener = loginStatusListener;
        this.encodingMethod = encodingMethod;
        this.decodingMethod = decodingMethod;

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        DeviceInterface.class,
                        new DeviceInterfaceInstanceCreator(factoryModel))
                .create();


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
    public RetrofitWebServiceFacade(RestAdapter.Log retrofitLog,
                                    OnlineStatusProvider onlineStatusProvider,
                                    WebSocketEventHandler wsEventHandler,
                                    LoginStatusListener loginStatusListener){
        this(retrofitLog, onlineStatusProvider, wsEventHandler, loginStatusListener, null, null);
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

    @Override
    public SecretKey getLocalUncryptedSessionKey() {
        return localUncryptedSessionKey;
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
        if (publicKey == null || privateKey == null || serverPublicKey == null || serverSessionKey == null)
            throw new RuntimeException("Missing key. Did you log in?");
    }

    /**
     * Request login
     * @param email email of the user
     * @param keysCallback public key and session key received from the server
     */
    @Override
    public void requestLogin(String email, final WebServiceCallback<Pair<byte[], byte[]>> keysCallback) {
        if(!onlineStatusProvider.isOnline()) return;

        KeyGenerator kg = new KeyGenerator(1024, "RSA");
        final PrivateKey prk = kg.getPrivateKey();
        final PublicKey puk = kg.getPublicKey();

        try {
            uncryptedRetrofit.requestLogin(email,
                    PublicKeyManager.convertToByte(puk),
                    new Callback<Pair<byte[], byte[]>>() {

                        @Override
                        public void success(Pair<byte[], byte[]> keys, Response response) {

                            if(keys.isErrorOccured()){
                                //TODO: check reason?
                                keysCallback.failure(new RuntimeException("Missing user"));
                            }

                            RetrofitWebServiceFacade.this.publicKey = puk;
                            RetrofitWebServiceFacade.this.privateKey = prk;

                            try {
                                RetrofitWebServiceFacade.this.serverPublicKey = PublicKeyManager.convertToKey(keys.getFirst());
                                RetrofitWebServiceFacade.this.serverSessionKey = SessionKeyManager.convertToKey(keys.getSecond());
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
    public void login(final String email, String password, final WebServiceCallback<User> result) {
        checkKeys();

        if(!onlineStatusProvider.isOnline()) return;

        try {
            Encrypt encrypt = new Encrypt(getServerPublicKey());
            encrypt.setCustomEncoding(encodingMethod);
            SecretKey sk = encrypt.getEncryptedSessionKey();
            localUncryptedSessionKey = encrypt.getSessionKey();

            loginRetrofit.login(email,
                    new Pair<byte[], String>(SessionKeyManager.convertToByte(sk), encrypt.encrypt(password)),
                    new Callback<User>() {

                        @Override
                        public void success(User user, Response response) {
                            try{
                                wsClient = new WSClient(email){
                                    @Override
                                    public void onOpen(ServerHandshake handshakedata) {
                                        super.onOpen(handshakedata);
                                        webSocketEventHandler.onWebSocketOpen();
                                    }

                                    @Override
                                    public void onMessage(String message) {
                                        super.onMessage(message);
                                        webSocketEventHandler.onWebSocketMessage(message);
                                    }

                                    @Override
                                    public void onClose(int code, String reason, boolean remote) {
                                        super.onClose(code, reason, remote);
                                        webSocketEventHandler.onWebSocketClose(code, reason, remote);
                                    }

                                    @Override
                                    public void onError(Exception ex) {
                                        super.onError(ex);
                                        webSocketEventHandler.onWebSocketError(ex);
                                    }
                                };
                            } catch (URISyntaxException e){
                                e.printStackTrace();
                            }

                            wsClient.establishConnection();
                            loggedUser = user;
                            loginStatusListener.onLogIn(user);
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
    public void continueMacroOnDevice(DeviceInterface fromDevice, DeviceInterface toDevice, RemoteExecutionDescriptor red, WebServiceCallback<String> result) {
        checkKeys();
        if(!onlineStatusProvider.isOnline()) return;
        cryptedRetrofit.continueMacroOnDevice(
                fromDevice.getId(),
                toDevice.getId(),
                red,
                convertCallback(result));
    }


    public WSClient getWebSocketClient() {
        return wsClient;
    }

    @Override
    public void logout(){
        if(wsClient == null) return;
        if(wsClient.isOpen())
            wsClient.closeConnection();
        publicKey = null;
        privateKey = null;
        localUncryptedSessionKey = null;
        serverSessionKey = null;
        serverPublicKey = null;
        loggedUser = null;
        loginStatusListener.onLogOut();
    }

    public UserInterface getLoggedUser() {
        return loggedUser;
    }

    public interface LoginStatusListener {
        public void onLogIn(UserInterface user);
        public void onLogOut();
    }
}
