package com.sinapsi.android.client;

import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.impl.User;

import java.util.List;

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

    /**
     * Esempio di una GET request. Immaginando che la url del server
     * sia "http://www.sinapsi.com" (per vedere come questa viene
     * prefissa vedere WebServiceFacade), e che l'email passata sia
     * "banane@ananas.org" allora la richiesta HTTP finale sara':
     * "GET http://www.sinapsi.com/devices?action=getAllDevicesByUser&email=banane%40ananas.org"
     * @param email
     * @return
     */
    @GET("/devices?action=getAllDevicesByUser")
    public List<DeviceInterface> getAllDevicesByUser(
            @Query(value = "email") String email);
    //TODO: ^^ this should be also a POST request and the
    //TODO: authentication token returned by loginUser()
    //TODO: must be sent as encrypted body


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

    @GET("/LoginServlet")
    public User login(@Query("email") String email,
                      @Query("password") String password);


}
