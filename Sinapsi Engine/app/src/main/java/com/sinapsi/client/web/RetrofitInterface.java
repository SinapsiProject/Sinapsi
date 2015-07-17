package com.sinapsi.client.web;

import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.AvailabilityMap;
import com.sinapsi.model.impl.CommunicationInfo;
import com.sinapsi.model.impl.Device;
import com.sinapsi.model.impl.SyncOperation;
import com.sinapsi.model.impl.TriggerDescriptor;
import com.sinapsi.model.impl.User;
import com.sinapsi.utils.Pair;
import com.sinapsi.utils.Triplet;

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

    public static final String LOGIN = "/login";
    public static final String REQUEST_LOGIN = "/request_login";
    public static final String REGISTER = "/register";
    public static final String DEVICES = "/devices";
    public static final String AVAILABLE_ACTIONS = "/available_actions";
    public static final String AVAILABLE_TRIGGERS = "/available_triggers";
    public static final String AVAILABLE_COMPONENTS = "/available_components";
    public static final String REMOTE_MACRO = "/remote_macro";
    public static final String MACROS = "/macro";

    public static final String ACTION = "?action=";

    public static final String ADD = "add";
    public static final String GET = "get";
    public static final String DEL = "del";
    public static final String PUSH = "push";


    /**
     * Pre login request
     *
     * @param email email of the user
     */
    @POST(REQUEST_LOGIN)
    public void requestLogin(
            @Query("email") String email,
            @Query("name") String deviceName,
            @Query("model") String deviceModel,
            @Body byte[] publicKey,
            Callback<Pair<byte[], byte[]>> keys);

    /**
     * Login request
     *
     * @param email           email of the user
     * @param passwordSession session key pf the user and password
     */
    @POST(LOGIN)
    public void login(
            @Query("email") String email,
            @Query("name") String deviceName,
            @Query("model") String deviceModel,
            @Body Pair<byte[], String> passwordSession,
            Callback<User> user);


    /**
     * Registration request
     *
     * @param email    email of the user
     * @param password password of the user
     */
    @POST(REGISTER)
    public void register(
            @Query("email") String email,
            @Body String password,
            Callback<User> user);


    /**
     * Device connected request
     *
     * @param email user email
     */
    @GET(DEVICES + ACTION + GET)
    public void getAllDevicesByUser(
            @Query("email") String email,
            @Query("name") String deviceName,
            @Query("model") String deviceModel,
            Callback<List<DeviceInterface>> devices);


    /**
     * Device registration request
     *
     * @param name    name of the device
     * @param model   model of the device
     * @param type    type of the device (mobile/desktop)
     * @param version version of the device
     * @param idUser  id of the device's user
     * @param device
     */
    @POST(DEVICES + ACTION + ADD)
    public void registerDevice(
            @Query("email") String email,
            @Query("name") String name,
            @Query("model") String model,
            @Query("type") String type,
            @Query("version") int version,
            @Body String idUser,
            Callback<Device> device);


    /**
     * Request the available actions
     *
     * @param idDevice the id of the device
     */
    @GET(AVAILABLE_ACTIONS)
    public void getAvailableActions(
            @Query("device") int idDevice,
            Callback<List<MacroComponent>> actions);


    /**
     * Send the available actions on the current device
     *
     * @param idDevice id device
     * @param actions  list of actions that are available
     */
    @POST(AVAILABLE_ACTIONS)
    public void setAvailableActions(
            @Query("device") int idDevice,
            @Body List<MacroComponent> actions,
            Callback<CommunicationInfo> result);


    /**
     * Request the available triggers
     *
     * @param idDevice the id of the device
     */
    @GET(AVAILABLE_TRIGGERS)
    public void getAvailableTriggers(
            @Query("device") int idDevice,
            Callback<List<MacroComponent>> triggers);





    @POST(AVAILABLE_COMPONENTS)
    public void setAvailableComponents(
            @Query("email") String email,
            @Query("name") String deviceName,
            @Query("model") String deviceModel,
            @Body Pair<List<TriggerDescriptor>,List<ActionDescriptor>> triggersAndActions,
            Callback<CommunicationInfo> result);


    @GET(AVAILABLE_COMPONENTS)
    public void getAvailableComponents(
            @Query("email") String email,
            @Query("name") String deviceName,
            @Query("model") String deviceModel,
            Callback<AvailabilityMap> result);




    /**
     * Send the available triggers on the current device
     *
     * @param idDevice id device
     * @param triggers list of actions that are available
     */
    @POST(AVAILABLE_TRIGGERS)
    public void setAvailableTriggers(
            @Query("device") int idDevice,
            @Body List<MacroComponent> triggers,
            Callback<CommunicationInfo> result);


    /**
     * Sends a macro to the server, and if it has the same id of another
     * macro, this is updated. If there are no macros with the same id,
     * the passed macro is added.
     *
     * @param email the user's email
     * @param macro the macro
     * @param result a callback with an integer containing the id of the macro
     *               added or updated, or -1 if there was an error
     */
    @POST(MACROS + ACTION + ADD)
    public void updateOrAddMacro(
            @Query("email") String email,
            @Query("name") String deviceName,
            @Query("model") String deviceModel,
            @Body MacroInterface macro,
            Callback<Integer> result);

    /**
     * Delete a macro
     *
     * @param email email of the user
     * @param macro the macro
     */
    @POST(MACROS + ACTION + DEL)
    public void deleteMacro(
            @Query("email") String email,
            @Query("name") String deviceName,
            @Query("model") String deviceModel,
            @Body MacroInterface macro,
            Callback<CommunicationInfo> result);

    /**
     * Gets all the macros from the server
     *
     * @param email the user's email
     */
    @GET(MACROS + ACTION + GET)
    public void getAllMacros(
            @Query("email") String email,
            @Query("name") String deviceName,
            @Query("model") String deviceModel,
            Callback<Pair<Boolean, List<MacroInterface>>> result);

    /**
     * Call this method to continue the execution of a macro on another device.
     *
     * @param from_device_id the device id
     * @param to_device_id   the destination device id
     * @param red            the remote execution descriptor
     */
    @POST(REMOTE_MACRO)
    public void continueMacroOnDevice(
            @Query("from_device") int from_device_id,
            @Query("to_device") int to_device_id,
            @Body RemoteExecutionDescriptor red,
            Callback<CommunicationInfo> result);


    /**
     * Sends a list of operation of changes to be pushed to the server
     *
     * @param email       the user email
     * @param deviceName  the device name
     * @param deviceModel the device model
     * @param changes     the changes to be pushed
     * @param result      the result
     */
    @POST(MACROS + ACTION + PUSH)
    public void pushChanges(
            @Query("email") String email,
            @Query("name") String deviceName,
            @Query("model") String deviceModel,
            @Body List<Pair<SyncOperation, MacroInterface>> changes,
            Callback<List<Pair<SyncOperation, Integer>>> result
    );

    /**
     * Calls for encryption test
     * @param email
     * @param object
     * @param result
     */
    @POST("/encryption_test")
    public void encryptionTest(
           @Query("email") String email,
           @Query("name") String deviceName,
           @Query("model") String deviceModel,
           @Body Object object,
           Callback<Object> result
   );

}
