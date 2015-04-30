package com.sinapsi.android.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sinapsi.android.AppConsts;
import com.sinapsi.model.DeviceInterface;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * WebService draft class.
 * This is now a class containing some example functions
 * to understand how RetroFit initializes a RetrofitInterface
 * variable.
 */
public class WebServiceFacade {


    private RetrofitInterface retrofit;

    public WebServiceFacade() {

        Gson gson = new GsonBuilder().create();

        //crea un nuovo rest adapter con le impostazioni di
        //default e l'url come endpoint
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(AppConsts.SINAPSI_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        //tramite la java reflection e le annotazioni, restAdapter
        //crea a runtime una implementazione valida per l'interfaccia
        // RetrofitInterface, la istanzia e la assegna alla variabile
        // retrofit.
        retrofit = restAdapter.create(RetrofitInterface.class);
    }

    public RetrofitInterface getRetrofit() {
        return retrofit;
    }
}
