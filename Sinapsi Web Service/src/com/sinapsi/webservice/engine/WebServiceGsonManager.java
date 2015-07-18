package com.sinapsi.webservice.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.sinapsi.engine.ComponentFactory;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.UserInterface;
import com.sinapsi.model.impl.Device;
import com.sinapsi.model.impl.FactoryModel;
import com.sinapsi.model.impl.Macro;
import com.sinapsi.model.impl.User;
import com.sinapsi.webshared.ComponentFactoryProvider;
import com.sinapsi.webshared.gson.DeviceInterfaceTypeAdapter;
import com.sinapsi.webshared.gson.MacroTypeAdapter;
import com.sinapsi.webshared.gson.UserInterfaceTypeAdapter;

import java.lang.reflect.Type;

/**
 * Manager used to easily initialize Gson object on Sinapsi Web Service
 */
public class WebServiceGsonManager {

	
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

		ComponentFactoryProvider cfProvider = new ComponentFactoryProvider() {
			@Override
			public ComponentFactory getComponentFactory() {
				return webServiceEngine.getComponentFactoryForUser(userId);
			}
		};

		Gson gson = defaultSinapsiGsonBuilder()	
				.registerTypeAdapter(MacroInterface.class, new MacroTypeAdapter(cfProvider))
				.registerTypeAdapter(Macro.class, new MacroTypeAdapter(cfProvider))
				.create();

		return gson;
	}

	public static GsonBuilder defaultSinapsiGsonBuilder(){
		return new GsonBuilder()
			.registerTypeAdapter(DeviceInterface.class, new DeviceInterfaceTypeAdapter())
			.registerTypeAdapter(Device.class, new DeviceInterfaceTypeAdapter())
			.registerTypeAdapter(User.class, new UserInterfaceTypeAdapter())
			.registerTypeAdapter(UserInterface.class, new UserInterfaceTypeAdapter());
	}
}
