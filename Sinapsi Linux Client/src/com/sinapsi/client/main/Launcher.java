package com.sinapsi.client.main;

import java.awt.Color;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Launcher extends Application {
	
	private Stage primaryStage;
	private BorderPane borderPane;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Sinapsi");
		
		initRootLayout();
		showLoginLayout();
		
	}
	
	/**
	 * Returns the main stage
	 * @return
	 */
	
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("../view/RootLayout.fxml"));
			BorderPane rootLayout = (BorderPane) loader.load();
			Image logo = new Image("../res/LogoScritta.png");
			ImageView imageView = new ImageView();
			imageView.setImage(logo);
			rootLayout.getChildren().add(imageView);
			
			
			// Show the scene of the RootLayout.fxml
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showLoginLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("../view/LoginLayout.fxml"));
			AnchorPane loginView = (AnchorPane) loader.load();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showRegistrationLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("../view/RegistrationLayout.fxml"));
			AnchorPane registrationView = (AnchorPane) loader.load();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showMacroEditorLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("../view/MacroEditorLayout.fxml"));
			AnchorPane macroEditorView = (AnchorPane) loader.load();			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
