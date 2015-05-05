package com.sinapsi.client;

import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.User;

import java.util.List;

/**
 * TODO: impl & doku
 */
public interface SinapsiWebServiceFacade {

    /**
     * TODO: doku
     * @param <T> expected response type
     */
    public interface WebServiceCallback<T>{
        void success(T t, Object response);
        void failure(Throwable error);
    }


    public void login(String email,
                      String password,
                      WebServiceCallback<User> result);

    public void register(String email,
                         String password,
                         WebServiceCallback<User> result);


    public void getAllDevicesByUser(UserInterface user,
                                    WebServiceCallback<List<DeviceInterface>> result);


    public void registerDevice(UserInterface user,
                               String deviceName,
                               String deviceModel,
                               String deviceType,
                               int deviceClientVersion,
                               WebServiceCallback<DeviceInterface> result);

    public void getAvailableActions(DeviceInterface device,
                                    WebServiceCallback<List<MacroComponent>> result);

    public void setAvailableActions(DeviceInterface device,
                                    List<MacroComponent> actions,
                                    WebServiceCallback<String> result);

    public void getAvailableTriggers(DeviceInterface device,
                                     WebServiceCallback<List<MacroComponent>> result);

    public void setAvailableTriggers(DeviceInterface device,
                                     List<MacroComponent> triggers,
                                     WebServiceCallback<String> result);

    public void continueMacroOnDevice(DeviceInterface device,
                                      RemoteExecutionDescriptor red,
                                      WebServiceCallback<String> result);


}
