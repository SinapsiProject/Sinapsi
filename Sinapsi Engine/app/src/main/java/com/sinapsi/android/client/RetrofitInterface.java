package com.sinapsi.android.client;

import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.impl.User;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Retrofit draft interface.
 * This is now an interface containing some example functions
 * with mockup RetroFit annotations.
 */
public interface RetrofitInterface {

    //TODO: eliminare esempi e mettere i veri metodi

    /**
     * Esempio di una GET request. Immaginando che la url del server
     * sia "http://www.sinapsi.com" (per vedere come questa viene
     * prefissa vedere WebServiceFacade), e che l'email passata sia
     * "banane@ananas.org" allora la richiesta HTTP finale sara':
     * "GET http://www.sinapsi.com/devices_connected?email=banane%40ananas.org"
     * @param email
     * @return
     */
    @GET("/devices_connected")
    public List<DeviceInterface> getAllDevicesByUser(
            @Query("email") String email);

    @POST("/devices_connected")
    public List<DeviceInterface> getAllDevicesByUser(
            @Query("email") String email,
            @Body String authToken);



    /**
     * Qui viene usata l'annotazione @POST, dove il body inviato (magari
     * per non inviare una password in chiaro) viene indicato con @Body
     * @param email
     * @param password
     * @return
     */
    @POST("/users?action=loginUser")
    public String loginUser(
            @Query("email") String email,
            @Body String password);


    //Ok, altro esempio di login, in modo che combaci con la servlet:
    @GET("/login")
    public void login(@Query("email") String email,
                      @Query("password") String password,
                      Callback<User> cb);

    /**
     * Call this method to continue the execution of a macro on another device.
     * @param id the device id
     * @param red the remote execution descriptor
     */
    @POST("/continue_macro")
    public void continueMacroOnDevice(@Query("device_id") int id,
                                      @Body RemoteExecutionDescriptor red);


}
