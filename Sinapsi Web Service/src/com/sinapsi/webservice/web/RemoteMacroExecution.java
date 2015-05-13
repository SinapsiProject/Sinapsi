package com.sinapsi.webservice.web;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.bgp.decryption.Decrypt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.server.websocket.Message;
import com.sinapsi.server.websocket.WebSocketServer;
import com.sinapsi.webservice.db.DeviceManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.utility.BodyReader;

/**
 * Servlet implementation class RemoteMacroExecutionServlet
 */
@WebServlet("/remote_macro")
public class RemoteMacroExecution extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// empty body
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    int idDevice = Integer.parseInt(request.getParameter("device"));
	    // read the encrypted jsoned body
        String encryptedJsonBody = BodyReader.read(request);
        KeysDBManager keysManager = new KeysDBManager();
        DeviceManager deviceManager = new DeviceManager();
        
        Gson gson = new Gson();
        
        try {
            // create the decrypter
            Decrypt decrypter = new Decrypt(keysManager.getPrivateKey(deviceManager.getUserEmail(idDevice)), 
                                            keysManager.getClientSessionKey((deviceManager.getUserEmail(idDevice))));
            // decrypt the jsoned body
            String jsonBody = decrypter.decrypt(encryptedJsonBody);
            RemoteExecutionDescriptor RED = gson.fromJson(jsonBody,new TypeToken<RemoteExecutionDescriptor>() {}.getType());
            
            if(deviceManager.getInfoDevice(idDevice).getKey().equals("Cloud") &&
               deviceManager.getInfoDevice(idDevice).getValue().equals("Sinapsi")) {
               
                //TODO: execute macro in the web service
            
            } else {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                String uri = "ws://localhost:8181" + request.getContextPath() + "/websocket";
                container.connectToServer(WebSocketServer.class, URI.create(uri));
                
                JsonObject jsonMessage = Json.createObjectBuilder()
                                            .add("data", gson.toJson(RED))
                                            .add("type", Message.REMOTE_MACRO_TYPE).build();
                
                WebSocketServer.send(new Message(jsonMessage), idDevice);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
       
	}

}
