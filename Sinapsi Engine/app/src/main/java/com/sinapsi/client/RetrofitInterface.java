package com.sinapsi.client;

import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.impl.User;

import java.security.PublicKey;
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

    /**
     * Login request
     * @param email email of the user
     * @param publicKey public key of the user, necessary to establish a secure connection
     * @param password password of the user
     * @return
     */
    @POST("login")
    public void login(
            @Query("email") String email,
            @Query("key") PublicKey publicKey,
            @Body String password,
            Callback<User> user);


    /**
     * Registration request
     * @param email email of the user
     * @param password password of the user
     */
    @POST("register")
    public void register(
            @Query("email") String email,
            @Body String password,
            Callback<User> user);


    /**
     * Device connected request
     * @param email user email
     * @param publicKey public key of the user
     * @return list of devices connected
     */
    @GET("/devices_connected")
    public void getAllDevicesByUser(
            @Query("email") String email,
            @Query("key") PublicKey publicKey,
            Callback<List<DeviceInterface>> devices);



    /**
     * Device registration request
     * @param name name of the device
     * @param model model of the device
     * @param type type of the device (mobile/desktop)
     * @param version version of the device
     * @param idUser id of the device's user
     */
    @POST("register_device")
    public void registerDevice(
            @Query("name") String name,
            @Query("model") String model,
            @Query("type") String type,
            @Query("version") int version,
            @Query("user") int idUser,
            @Query("key") PublicKey publicKey,
            Callback<DeviceInterface> result);

    /**
     * Request the available actions
     * @param idDevice
     * @param publicKey
     */
    @GET("available_actions")
    public void getAvailableActions(
            @Query("device") int idDevice,
            @Query("key") PublicKey publicKey,
            Callback<List<MacroComponent>> actions);

    /**
     * Send the available actions on the current device
     * @param idDevice id device
     * @param publicKey public key of the user
     * @param actions list of actions that are available
     */
    @POST("available_actions")
    public void setAvailableActions(
            @Query("device") int idDevice,
            @Query("key") PublicKey publicKey,
            @Body List<MacroComponent> actions,
            Callback<String> result);

    /**
     * Request the available triggers
     * @param idDevice
     * @param publicKey
     */
    @GET("available_triggers")
    public void getAvailableTriggers(
            @Query("device") int idDevice,
            @Query("key") PublicKey publicKey,
            Callback<List<MacroComponent>> triggers);

    /**
     * Send the available triggers on the current device
     * @param idDevice id device
     * @param publicKey public key of the user
     * @param triggers list of actions that are available
     */
    @POST("available_triggers")
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
    @POST("/continue_macro")
    public void continueMacroOnDevice(
            @Query("device_id") int id,
            @Body RemoteExecutionDescriptor red,
            Callback<String> result);

}
