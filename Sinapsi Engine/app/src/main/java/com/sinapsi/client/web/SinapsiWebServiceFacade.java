package com.sinapsi.client.web;

import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
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

/**
 * Platform independent interface, containing a collection of web methods.
 */
public interface SinapsiWebServiceFacade {


    /**
     * Platform independent version of Retrofit's Callback
     *
     * @param <T> expected response type
     */
    public interface WebServiceCallback<T> {
        void success(T t, Object response);

        void failure(Throwable error);

        //TODO: void offline(); //to be called from RetrofitWebServiceFacade when is offline
    }

    /**
     * Request login
     *
     * @param email email of the user
     * @param keys  public key and session key recived from the server
     */
    public void requestLogin(String email, String deviceName, String deviceModel,
                             WebServiceCallback<Pair<byte[], byte[]>> keys);

    /**
     * Logs in the user
     *
     * @param email    the email
     * @param password the password
     * @param result   the User instance returned by the web service
     */
    public void login(String email,
                      String password,
                      String deviceName,
                      String deviceModel,
                      WebServiceCallback<User> result);

    /**
     * Registers the users
     *
     * @param email    the email
     * @param password the password
     * @param result   the User instance returned by the web service
     */
    public void register(String email,
                         String password,
                         WebServiceCallback<User> result);


    /**
     * Gets all the device of the specified owner
     *
     * @param user   the user
     * @param result a List of DeviceInterface instances
     */
    public void getAllDevicesByUser(UserInterface user,
                                    String deviceName,
                                    String deviceModel,
                                    WebServiceCallback<List<DeviceInterface>> result);


    /**
     * Adds a new device to the user's collection of devices
     * on the web service
     *
     * @param user                the user
     * @param deviceName          the name of the device
     * @param deviceModel         the model of the device
     * @param deviceType          the type of the device
     * @param deviceClientVersion the version of the Sinapsi Engine running on the device
     * @param result              the DeviceInterface instance returned by the web service
     */
    public void registerDevice(UserInterface user,
                               String emailUser,
                               String deviceName,
                               String deviceModel,
                               String deviceType,
                               int deviceClientVersion,
                               WebServiceCallback<Device> result);

    /**
     * Gets all the macros saved in the web service
     *
     * @param result the macros result
     */
    public void getAllMacros(DeviceInterface device, WebServiceCallback<Pair<Boolean, List<MacroInterface>>> result);


    /**
     * Sets the availability of triggers and actions of the current device
     *
     * @param device   the caller device
     * @param triggers the available triggers
     * @param actions  the available actions
     * @param result   the result callback
     */
    void setAvailableComponents(DeviceInterface device,
                                List<TriggerDescriptor> triggers,
                                List<ActionDescriptor> actions,
                                WebServiceCallback<CommunicationInfo> result);


    /**
     * Downloads a list of triplets containing a device with related lists of descriptors
     * of available triggers and actions on that device. Useful when, in the editor, there
     * is the need to fetch the available components for every device in the account in order
     * tho show them to the user.
     *
     * @param device the caller device
     * @param result the result callback
     */
    void getAvailableComponents(DeviceInterface device, WebServiceCallback<AvailabilityMap> result);

    /**
     * Asks the server to continued the specified macro on a remote device
     *
     * @param fromDevice the device that sends the request
     * @param toDevice   the device on which the macro will continue
     * @param red        a descriptor containing infos about the execution that needs
     *                   to be continued
     * @param result     call Bach
     */
    public void continueMacroOnDevice(DeviceInterface fromDevice,
                                      DeviceInterface toDevice,
                                      RemoteExecutionDescriptor red,
                                      WebServiceCallback<CommunicationInfo> result);


    /**
     * Sends a list of operation of changes to be pushed to the server
     *
     * @param device   this device
     * @param changes  the list of changes
     * @param callback a list of pairs of SyncOperation done by the server and the ids of the macro
     */
    public void pushChanges(DeviceInterface device,
                            List<Pair<SyncOperation, MacroInterface>> changes,
                            WebServiceCallback<List<Pair<SyncOperation, Integer>>> callback);

    /**
     * Makes a logout from the server. The client should delete
     * any keys and close any open connection after this.
     */
    public void logout();

    /**
     * Encrytion test
     *
     * @param email
     * @param callback
     */
    public void encryptionTest(String email, String deviceName, String deviceModel, WebServiceCallback<Object> callback);
}
