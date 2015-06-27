package com.sinapsi.webshared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.sinapsi.client.web.gson.DeviceInterfaceInstanceCreator;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.webshared.gson.MacroTypeAdapter;

import java.lang.reflect.Type;

/**
 * Manager used to easily initialize Gson object on Sinapsi Web Service
 */
public class WebServiceGsonManager {

    //TODO: spostare WebServiceGsonManager nei pacchetti esclusivi del web service ed importare il *vero* WebServiceEngine
    public interface WebServiceEngine {  //TODO: Cancellare questa interfaccia; qui la uso solo per non farmi gli uscire gli errori

        public ComponentFactory getComponentFactoryForUser(int userId);
    }

    //TODO: COME SI USA: metti un oggetto di questa classe *almeno* nelle servlet dove invii/ricevi oggetti MacroInterface,
    //TODO: (ad es. private WebServiveGsonManager gsonManager = new WebServiceGsonManager(webServiceEngine);
    //TODO: dopodiché, invece di usare il normale oggetto gson per serializzare/deserializzare cose, (tipo gson.fromJson(...) )
    //TODO: usare gsonManager.getGsonForUser(userid).fromJson(...) ( o .toJson(...) )

    // questo aggiunge agli oggetti gson uno speciale TypeAdapter che serializza e deserializza le macro:

    // 1) usando il component factory (tutti i component devono essere creati coi ComponentFactory per funzionare correttamente)

    // 2) impedendo riferimenti ciclici che potrebbero far sputare Gson (ad es. la macro X contiene trigger T, che al suo interno
    //                          ha un riferimento alla macro X - anzi, deve averlo)

    // 3) riducendo di mooolto la dimensione della stringa JSON (solo le cose effettivamente necessarie alla ricostruzione della
    //                          macro vengono inserite nel json: sono le stesse che vengono salvate nei db locali e del server)


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
