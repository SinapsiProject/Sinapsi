package com.sinapsi.webservice.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bgp.decryption.Decrypt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.server.websocket.Message;
import com.sinapsi.server.websocket.WebSocketClient;
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
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    int deviceTarget = Integer.parseInt(request.getParameter("to_device"));
	    int fromDevice = Integer.parseInt(request.getParameter("from_device"));
	    PrintWriter out = response.getWriter();
	    // read the encrypted jsoned body
        String encryptedJsonBody = BodyReader.read(request);
        KeysDBManager keysManager = new KeysDBManager();
        DeviceManager deviceManager = new DeviceManager();
       
        Gson gson = new Gson();
        
        try {
            // create the decrypter
            Decrypt decrypter = new Decrypt(keysManager.getPrivateKey(deviceManager.getUserEmail(fromDevice)), 
                                            keysManager.getClientSessionKey((deviceManager.getUserEmail(fromDevice))));
            // decrypt the jsoned body
            String jsonBody = decrypter.decrypt(encryptedJsonBody);
            RemoteExecutionDescriptor RED = gson.fromJson(jsonBody,new TypeToken<RemoteExecutionDescriptor>() {}.getType());
            
            if(deviceManager.getInfoDevice(deviceTarget).getKey().equals("Cloud") &&
               deviceManager.getInfoDevice(deviceTarget).getValue().equals("Sinapsi")) {
               
                //TODO: execute macro in the web service
            
            } else {
                //message to send to the remote device containing also the remote execution descriptor 
                JsonObject message = Json.createObjectBuilder()
                                  .add("data", gson.toJson(RED))
                                  .add("to", deviceTarget)
                                  .add("type", Message.REMOTE_MACRO_TYPE).build();
                
                // create new client endpoint, passing the name and the uri of the server endpoint
                WebSocketClient clientEndpoint = new WebSocketClient("pi", new URI("ws://localhost:8080/websocket"));
               
                // comunication between client and server is open
                if(clientEndpoint.getSession().isOpen()) {
                    // send the json of the message containing the RED object to server endpoint
                    WebSocketClient.send(clientEndpoint.getSession(), new Message(message));
                    
                } else {
                    //TODO: comunicate the sendere that the device target is not connected
                }
               
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
       
	}

}
