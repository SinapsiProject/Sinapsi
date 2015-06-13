package com.sinapsi.webservice.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.java_websocket.WebSocket;

import com.bgp.decryption.Decrypt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinapsi.engine.MacroEngine;
import com.sinapsi.engine.execution.RemoteExecutionDescriptor;
import com.sinapsi.model.UserInterface;
import com.sinapsi.webservice.db.DeviceDBManager;
import com.sinapsi.webservice.db.KeysDBManager;
import com.sinapsi.webservice.engine.WebServiceEngine;
import com.sinapsi.webservice.utility.BodyReader;
import com.sinapsi.webservice.websocket.Server;
import com.sinapsi.wsproto.SinapsiMessageTypes;
import com.sinapsi.wsproto.WebSocketMessage;

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
	    Server wsserver = (Server) getServletContext().getAttribute("wsserver");
	    WebServiceEngine engine = (WebServiceEngine) getServletContext().getAttribute("engine");

	    // read the encrypted jsoned body
        String encryptedJsonBody = BodyReader.read(request);
        KeysDBManager keysManager = (KeysDBManager) getServletContext().getAttribute("keys_db");
        DeviceDBManager deviceManager = (DeviceDBManager) getServletContext().getAttribute("devices_db");
       
        Gson gson = new Gson();
        
        try {
            // create the decrypter
            Decrypt decrypter = new Decrypt(keysManager.getServerPrivateKey(deviceManager.getUserEmail(fromDevice)), 
                                            keysManager.getUserSessionKey((deviceManager.getUserEmail(fromDevice))));
            //decrypt the jsoned body
            String jsonBody = decrypter.decrypt(encryptedJsonBody);
            RemoteExecutionDescriptor RED = gson.fromJson(jsonBody,new TypeToken<RemoteExecutionDescriptor>() {}.getType());
            
            if(deviceManager.getInfoDevice(deviceTarget).getKey().equals("Cloud") &&
               deviceManager.getInfoDevice(deviceTarget).getValue().equals("Sinapsi")) {
               
                UserInterface user = deviceManager.getUserDevice(fromDevice);
            	MacroEngine cloudMacroEngine = engine.getEngineForUser(user);

            	cloudMacroEngine.continueMacro(RED);
            
            } else {                
                WebSocket clientTarget = wsserver.getClient(deviceManager.getUserEmail(deviceTarget));
                WebSocketMessage message = new WebSocketMessage(SinapsiMessageTypes.REMOTE_EXECUTION_DESCRIPTOR, RED);
                wsserver.send(clientTarget, gson.toJson(message));  
                //TODO: define comunication error with the client policy
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
       
	}

}
