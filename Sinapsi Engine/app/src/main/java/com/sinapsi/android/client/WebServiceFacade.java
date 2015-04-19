package com.sinapsi.android.client;

import com.sinapsi.model.DeviceInterface;

import java.util.List;

import retrofit.RestAdapter;

/**
 * WebService draft class.
 * This is now a class containing some example functions
 * to understand how RetroFit initializes a RetrofitInterface
 * variable.
 */
public class WebServiceFacade {
    private RetrofitInterface retrofit;

    public WebServiceFacade(){
        String exampleUrl = "http://www.sinapsi.com";

        //TODO: set a GSON converter with type adapters to convert
        //TODO:     json strings to Sinapsi model objects. Then
        //TODO:     add it to restAdapter by calling setConverter()

        //crea un nuovo rest adapter con le impostazioni di
        //default e l'url come endpoint
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(exampleUrl)
                .build();

        //tramite la java reflection e le annotazioni, restAdapter
        //crea a runtime una implementazione valida per l'interfaccia
        // RetrofitInterface, la istanzia e la assegna alla variabile
        // retrofit.
        retrofit = restAdapter.create(RetrofitInterface.class);
    }

    public void exampleCall(){
        List<DeviceInterface> devices= retrofit.getAllDevicesByUser("banana@ananas.com");

        //do something
    }
}
