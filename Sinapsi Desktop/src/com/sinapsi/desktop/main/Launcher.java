/**
 * @author Marco Grillo
 */

package com.sinapsi.desktop.main;


import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Launcher extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Sinapsi");
		this.primaryStage.setResizable(false);
			
		initRootLayout();
		initLoginLayout();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	
	// Root layout (main layout) loader
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("../view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			// Showing the scene containing the root Layout
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// Login layout loader
	public void initLoginLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("../view/LoginLayout.fxml"));
			AnchorPane loginLayout = (AnchorPane) loader.load();
			
			HBox horizontalBox = new HBox();
			horizontalBox.setPrefWidth(600);
			horizontalBox.setPrefHeight(300);
			
			Image img = new Image(Launcher.class.getResourceAsStream("../view/logo.png"), 700, 400, true, true);
			ImageView imgView = new ImageView(img);			
	        horizontalBox.getChildren().add(imgView);
		
			// Sets the login layout into the center of the root layout
			rootLayout.setCenter(loginLayout);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	// About layout loader
	public void initAboutLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("../view/AboutLayout.fxml"));
			AnchorPane aboutLayout = (AnchorPane) loader.load();
					
			// Sets the about layout into the center of the root layout
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	// Registration layout loader
	public void initRegistrationLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("../view/RegistrationLayout.fxml"));
			AnchorPane registrationLayout = (AnchorPane) loader.load();
			
			// Sets the registration layout into the center of the root layout if the register button is pressed
			rootLayout.setCenter(registrationLayout);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	// Macro editor layout loader
	public void initMacroEditorLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("../view/MacroEditorLayout.fxml"));
			AnchorPane macroEditorLayout = (AnchorPane) loader.load();
			
			// Sets the macro editor layout into the center of the root layout after login has been verified
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
