package com.sinapsi.webservice.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.webshared.ComponentFactoryProvider;
import com.sinapsi.webshared.gson.MacroTypeAdapter;
import java.lang.reflect.Type;

/**
 * Manager used to easily initialize Gson object on Sinapsi Web Service
 */
public class WebServiceGsonManager {
    private FactoryModel factoryModel;
    private WebServiceEngine webServiceEngine;

    public WebServiceGsonManager(WebServiceEngine webServiceEngine) {
        this.webServiceEngine = webServiceEngine;
    }

    /**
     * This will generate a gson object with all the required type adapters
     *
     * @param userId the user id
     * @return a gson object
     */
    public Gson getGsonForUser(final int userId) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        UserInterface.class,
                        new InstanceCreator<UserInterface>() {
                            @Override
                            public UserInterface createInstance(Type type) {
                                return factoryModel.newUser(-1, null, null);
                            }
                        })
                .registerTypeAdapter(
                        MacroInterface.class,
                        new MacroTypeAdapter(
                                new ComponentFactoryProvider() {
                                    @Override
                                    public ComponentFactory getComponentFactory() {
                                        return webServiceEngine.getComponentFactoryForUser(userId);
                                    }
                                }
                        ))
                .create();

        return gson;
    }
}