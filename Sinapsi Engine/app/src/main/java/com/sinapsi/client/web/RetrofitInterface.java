package com.sinapsi.client.web;

import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.impl.User;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Retrofit interface
 */
public interface RetrofitInterface {

    public static final String LOGIN = "login";
    public static final String REQUEST_LOGIN = "request_login";
    public static final String REGISTER = "register";
    public static final String DEVICES = "devices";
    public static final String AVAILABLE_ACTIONS = "available_actions";
    public static final String AVAILABLE_TRIGGERS = "available_triggers";
    public static final String CONTINUE_MACRO = "continue_macro";

    public static final String ACTION = "?action=";

    public static final String ADD = "add";
    public static final String GET = "get";


    /**
     * Pre login request
     * @param email email of the user
     */
    @POST(REQUEST_LOGIN)
    public void requestLogin(
            @Query("email") String email,
            Callback<HashMap<String, String>> keys);

    /**
     * Login request
     * @param email email of the user
     * @param password password of the user
     */
    @POST(LOGIN)
    public void login(
            @Query("email") String email,
            @Body String password,
            Callback<User> user);


    /**
     * Registration request
     * @param email email of the user
     * @param password password of the user
     */
    @POST(REGISTER)
    public void register(
            @Query("email") String email,
            @Body String password,
            Callback<User> user);


    /**
     * Device connected request
     * @param email user email
     */
    @GET(DEVICES+ACTION+GET)
    public void getAllDevicesByUser(
            @Query("email") String email,
            Callback<List<DeviceInterface>> devices);


    /**
     * Device registration request
     * @param name name of the device
     * @param model model of the device
     * @param type type of the device (mobile/desktop)
     * @param version version of the device
     * @param idUser id of the device's user

     */
    @GET(DEVICES+ACTION+ADD)
    public void registerDevice(
            @Query("name") String name,
            @Query("model") String model,
            @Query("type") String type,
            @Query("version") int version,
            @Query("user") int idUser,
            Callback<DeviceInterface> result);


    /**
     * Request the available actions
     * @param idDevice the id of the device
     */
    @GET(AVAILABLE_ACTIONS)
    public void getAvailableActions(
            @Query("device") int idDevice,
            Callback<List<MacroComponent>> actions);


    /**
     * Send the available actions on the current device
     * @param idDevice id device
     * @param actions list of actions that are available
     */
    @POST(AVAILABLE_ACTIONS)
    public void setAvailableActions(
            @Query("device") int idDevice,
            @Body List<MacroComponent> actions,
            Callback<String> result);


    /**
     * Request the available triggers
     * @param idDevice the id of the device
     */
    @GET(AVAILABLE_TRIGGERS)
    public void getAvailableTriggers(
            @Query("device") int idDevice,
            Callback<List<MacroComponent>> triggers);


    /**
     * Send the available triggers on the current device
     * @param idDevice id device
     * @param publicKey public key of the user
     * @param triggers list of actions that are available
     */
    @POST(AVAILABLE_TRIGGERS)
    public void setAvailableTriggers(
            @Query("device") int idDevice,
            @Query("key") PublicKey publicKey,
            @Body List<MacroComponent> triggers,
            Callback<String> result);


    /**
     * Call this method to continue the execution of a macro on another device.
     * @param id the device id
     * @param red the remote execution descriptor
     */
    @POST(CONTINUE_MACRO)
    public void continueMacroOnDevice(
            @Query("device_id") int id,
            @Body RemoteExecutionDescriptor red,
            Callback<String> result);

}
