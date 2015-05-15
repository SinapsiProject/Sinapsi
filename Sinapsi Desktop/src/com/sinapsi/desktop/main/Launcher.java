/**
 * @author Marco Grillo
 */

package com.sinapsi.desktop.main;


import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class Launcher extends Application {
	
	private Stage primaryStage;
	// private Stage secondaryStage;
	
	// Panes
	private GridPane root;
	
	// Buttons
	private Button signIn;
	
	// Labels
	private Label emailLabel;
	private Label passwordLabel;
	private Label copyrightLabel;
	private Label aboutLabel;
	private TextFlow registerLabel;
	
	// TextFields
	private TextField emailField;
	private PasswordField passwordField;
	
	// Hbox
	private HBox hboxSignIn;
	
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
		this.root = new GridPane();		
		
//		Image logo = new Image(getClass().getResourceAsStream("../res/rsz_31logo.png"));
//		ImageView logoView = new ImageView();
//		logoView.setImage(logo);
		
		// Setting email label and textfield
		emailLabel = new Label("Email");
		emailField = new TextField();
		
		// Setting password label and password field
		passwordLabel = new Label("Password");
		passwordField = new PasswordField();
		
		// Setting registration label (clickable) and color
		registerLabel = new TextFlow(new Text("New to Sinapsi?"), new Hyperlink("Register!"));
		
		// Setting button text 
		signIn = new Button("Sign in");
		// and hbox 
		hboxSignIn = new HBox(10);
		hboxSignIn.setAlignment(Pos.BOTTOM_RIGHT);
		hboxSignIn.getChildren().add(signIn);
		
		// Setting about label
		aboutLabel = new Label("About");
		aboutLabel.setTextFill(Color.web("#256581"));
					
	
		root.setAlignment(Pos.CENTER);
		root.setHgap(5);
		root.setVgap(15);
		root.setPadding(new Insets(50, 50, 50, 50));
		
		root.add(emailLabel, 0, 2);
		root.add(emailField, 0, 3);
		root.add(passwordLabel, 0, 4);
		root.add(passwordField, 0, 5);
		root.add(registerLabel, 0, 6);
		// root.add(aboutLabel, 0, 7, 10, 1);
		root.add(hboxSignIn, 0, 9);
		
		
		
		
		
		Scene firstScene = new Scene(root, 800, 600,Color.GRAY);
		
		primaryStage.setScene(firstScene);
		primaryStage.show();
	}
}	
	
	