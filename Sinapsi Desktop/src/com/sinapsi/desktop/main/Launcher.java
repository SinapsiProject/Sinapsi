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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
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
	private BorderPane root;
	private GridPane grid;
	private GridPane logoPane;
	private StackPane tutorialLayout;
	
	// Buttons
	private Button signIn;
	private Button tutorialButton;
	
	// Labels
	private Label emailLabel;
	private Label passwordLabel;
	private Label copyrightLabel;
	private TextFlow registerLabel;
	
	// TextFields
	private TextField emailField;
	private PasswordField passwordField;
	
	// Hbox
	private HBox hboxSignIn;	

	// Vbox
	private VBox vboxLogo;
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
		this.grid = new GridPane();		
		this.root = new BorderPane();
		this.logoPane = new GridPane();
		
		// Setting logo 
		Image logo = new Image("file:res/rsz_31logo.png",600,400,true,true);
		ImageView logoView = new ImageView();
		logoView.setImage(logo);
		// and vbox
		vboxLogo = new VBox(50);
		vboxLogo.getChildren().add(logoView);
		
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
		hboxSignIn.setAlignment(Pos.CENTER);
		hboxSignIn.getChildren().add(signIn);
		
		// Setting tutorial button
		tutorialButton = new Button();
		tutorialButton.setStyle("-fx-background-radius: 5em; " +
				                "-fx-min-width: 3px; " +
				                "-fx-min-height: 3px; " +
				                "-fx-max-width: 3px; " +
				                "-fx-max-height: 3px;");
		
		// LogoPane		
		logoPane.setAlignment(Pos.TOP_CENTER);
		logoPane.setVgap(10);
		logoPane.add(logoView, 0, 6);
		
		// LoginPane
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(15);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
			
		grid.add(emailLabel, 0, 2);
		grid.add(emailField, 0, 3);
		grid.add(passwordLabel, 0, 4);
		grid.add(passwordField, 0, 5);
		grid.add(registerLabel, 0, 6);
		grid.add(hboxSignIn, 0, 9);
		
		root.setTop(logoPane);
		root.setCenter(grid);
		Scene firstScene = new Scene(root, 800, 600);
		
		primaryStage.setScene(firstScene);
		primaryStage.show();
	}
}	
	
	