package com.sinapsi.desktop.implementation;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class RootLayoutImplementation extends Application {

	/**
	 * Layout components 
	 */
	
	// Stages
		private Stage primaryStage;
		private Stage tutorialStage;
		private Stage registerStage;
					
		// Panes
		private BorderPane root;
		private GridPane grid;
		private GridPane logoPane;
		private GridPane tutorialButtonPane;
		private BorderPane tutorialPane;
					
		// Buttons
		private Button signIn;
		private Button tutorialButton;
		private Button switchPageNext;
		private Button switchPagePrevious;
					
		// Labels
		private Label emailLabel;
		private Label passwordLabel;
		
		// TextFlow
		private TextFlow registerLabel;
				
		// Hyperlinks
		private Hyperlink registerLink;
					
		// TextFields
		private TextField emailField;
		private PasswordField passwordField;
		
		// Hbox
		private HBox hboxSignIn;	
		private HBox hboxSwitch;

		// Vbox
		private VBox vboxLogo;
	
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Sinapsi Login");
		this.primaryStage.setResizable(false);
		
		initLayout();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	
	/**
	 * Main layout launcher
	 */
	
	public void initLayout() {
		this.grid = new GridPane();		
		this.root = new BorderPane();
		this.logoPane = new GridPane();
		this.tutorialButtonPane = new GridPane();
		
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
		registerLink = new Hyperlink("Register!");
		registerLink.setTextFill(Color.web("#256581"));
		registerLabel = new TextFlow(new Text("New to Sinapsi?"), registerLink);
		
		// Setting button text 
		signIn = new Button("Sign in");

		// and hbox 
		hboxSignIn = new HBox(10);
		hboxSignIn.setAlignment(Pos.CENTER);
		hboxSignIn.getChildren().add(signIn);
		
		// Setting tutorial button
		Image tutorialLogo = new Image("file:res/tutorial.png");
		ImageView tutorialButtonView = new ImageView();
		tutorialButtonView.setImage(tutorialLogo);
		tutorialButton = new Button();
		tutorialButton.setGraphic(tutorialButtonView);
		tutorialButton.setStyle(
				"-fx-background-radius: 5em; " +
                "-fx-min-width: 30px; " +
                "-fx-min-height: 30px; " +
                "-fx-max-width: 30px; " +
                "-fx-max-height: 30px;"+
                "-fx-effect: dropshadow(three-pass-box, gray, 4, 0.5, 0, 0)"
		);
		
			
		
		// BorderPane Top	
		logoPane.setAlignment(Pos.TOP_CENTER);
		logoPane.setVgap(10);
		logoPane.add(logoView, 0, 6);
		
		
		// BorderPane Center
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
		
		// BorderPane Bottom
		tutorialButtonPane.setAlignment(Pos.BOTTOM_RIGHT);
		tutorialButtonPane.setPadding(new Insets(25, 25, 25, 25));
		tutorialButtonPane.setHgap(10);
		tutorialButtonPane.add(tutorialButton, 0, 1);
		
		/**
		 * 
		 * All the actions of the login layout
		 * 
		 */
		
		// Tutorial Button Handler	
		tutorialButton.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				tutorialPane = new BorderPane();
				
				switchPagePrevious = new Button("Previous");
					switchPagePrevious.setPrefWidth(180);
				switchPageNext = new Button("Next");
					switchPageNext.setPrefWidth(180);
					
				hboxSwitch = new HBox();
				hboxSwitch.setAlignment(Pos.CENTER);
				hboxSwitch.setPadding(new Insets(52,52,55,52));
				hboxSwitch.setSpacing(50);
				
				hboxSwitch.getChildren().addAll(switchPagePrevious, switchPageNext);
				tutorialPane.setBottom(hboxSwitch);
				
				tutorialStage = new Stage();
				tutorialStage.setTitle("Sinapsi Tutorial");
				tutorialStage.setScene(new Scene(tutorialPane, 400, 600));
				tutorialStage.setResizable(false);
				tutorialStage.show();
			}
		});
		
		registerLink.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				registerStage = new Stage();
				registerStage.setTitle("Registration");
				registerStage.setResizable(false);
				registerStage.show();
			}
		});
		
		
		root.setBottom(tutorialButtonPane);
		root.setTop(logoPane);
		root.setCenter(grid);
		Scene loginScene = new Scene(root, 800, 600);
		
		primaryStage.setScene(loginScene);
		primaryStage.setTitle("Sinapsi Login");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}
