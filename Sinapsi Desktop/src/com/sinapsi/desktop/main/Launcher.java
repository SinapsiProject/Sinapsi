/**
 * @author Marco Grillo
 */

package com.sinapsi.desktop.main;


import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Launcher extends Application {
	
	private Stage primaryStage;
	// private Stage secondaryStage;
	
	// Panes
	private BorderPane root;
	private AnchorPane loginLayout;
	private GridPane gridPane;
	
	// Buttons
	private Button signIn;
	
	// Labels
	private Label emailLabel;
	private Label passwordLabel;
	private Label registerLabel;
	private Label copyrightLabel;
	private Label aboutLabel;
	
	// TextFields
	private TextField emailField;
	private PasswordField passwordField;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Sinapsi Login");
		this.primaryStage.setResizable(false);
			
		initRootLayout();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void initRootLayout() {
		this.root = new BorderPane();
		this.loginLayout = new AnchorPane();
		Scene firstScene = new Scene(root, 800, 600,Color.GRAY);
		
//		Image logo = new Image(getClass().getResourceAsStream("../res/rsz_31logo.png"));
//		ImageView logoView = new ImageView();
//		logoView.setImage(logo);
//      root.setTop(logoView);
		
		emailLabel = new Label("Email");
		emailField = new TextField("Insert email...");
		
		passwordLabel = new Label("Password");
		passwordField = new PasswordField();
		
		gridPane = new GridPane();
		gridPane.add(emailLabel, 0, 0);
		gridPane.add(emailField, 0, 1);
		gridPane.add(passwordLabel, 0, 2);
		gridPane.add(passwordField, 0, 3);
		
		root.getChildren().add(gridPane);
		root.setCenter(gridPane);
		
		
		
		
		
		
		primaryStage.setScene(firstScene);
		primaryStage.show();
	}
}	
	
	