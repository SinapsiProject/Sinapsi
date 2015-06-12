package com.sinapsi.desktop.controller;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Enumeration;
import java.util.HashMap;

import org.java_websocket.handshake.ServerHandshake;

import retrofit.RetrofitError;
import javafx.scene.control.Button;

import com.sinapsi.client.web.*;
import com.sinapsi.client.web.SinapsiWebServiceFacade.WebServiceCallback;
import com.sinapsi.client.websocket.WSClient;
import com.sinapsi.desktop.log.*;
import com.sinapsi.model.impl.User;

public class LayoutController {
	
	private RetrofitWebServiceFacade retrofitService;
	private WSClient wsClient;

	public LayoutController(Button button) {
		
		try {
			wsClient = new WSClient() { 

				@Override
				public void onOpen(ServerHandshake handshakedata) {
					super.onOpen(handshakedata);
				}

				@Override
				public void onMessage(String message) {
					super.onMessage(message);
					handleWsMessage(message, true);
				}

				@Override
				public void onError(Exception ex) {
					super.onError(ex);
				}

				@Override
				public void onClose(int code, String reason, boolean remote) {
					super.onClose(code, reason, remote);
				}

				public void handleWsMessage(String message, boolean firstCall) {
					// TODO 
				}
			};

			OnlineStatusProvider onlineStatusProvider = new OnlineStatusProvider() {

				@Override
				public boolean isOnline() {
					try {
						Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
						while (interfaces.hasMoreElements()) {
							NetworkInterface interf = interfaces.nextElement();
							if (interf.isUp() && !interf.isLoopback())
								return true;
						}

					} catch(SocketException e) {
						e.printStackTrace();
					}
					return false;
				}
			};

			retrofitService = new RetrofitWebServiceFacade(new DesktopClientLog(),
					onlineStatusProvider, 
					wsClient);
		}
		catch(URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void login(String email, String password) {
		
		retrofitService.requestLogin(email, new WebServiceCallback<HashMap.SimpleEntry<byte[],byte[]>>() {
		
			@Override
			public void success(SimpleEntry<byte[], byte[]> t, Object response) {
				retrofitService.login(email, password, new WebServiceCallback<User>() {

					@Override
					public void success(User t, Object response) {
						if(t.isErrorOccured()) {
							System.out.println("fanculo");
							
						}
						else {
							//wsClient.establishConnection();
							// Info user
							System.out.println("Fanculo, user id: " + t.getId());
							
						}
					}

					@Override
					public void failure(Throwable error) {
						handleRetrofitError(error);
					}

				}); 				
			}

			@Override
			public void failure(Throwable error) {
				handleRetrofitError(error);
			}

		});
	}

	public void handleRetrofitError(Throwable t) {
		
		RetrofitError error = (RetrofitError) t;
		error.printStackTrace();
		String errstring = "An error occurred while communicating with the server.\n";

		String errtitle = "Error: " + error.getKind().toString();

		switch (error.getKind()) {
		case NETWORK:
			errstring += "Network error";
			break;
		case CONVERSION:
			errstring += "Conversion error";

			break;
		case HTTP:
			errstring += "HTTP Error " + error.getResponse().getStatus();
			break;
		case UNEXPECTED:
			errstring += "An unexpected error occurred";
			break;
		}
	}
	
	public void logout() {
		retrofitService.logout();
	}
}
