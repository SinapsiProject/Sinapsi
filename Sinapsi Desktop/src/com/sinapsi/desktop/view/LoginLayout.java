package com.sinapsi.desktop.view;



import com.sinapsi.desktop.controller.LayoutController;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class LoginLayout extends Application {

	/**
	 * Layout components 
	 */
		// Controller 
		private LayoutController controller;
	
		// Stages
		private Stage primaryStage;
		private Stage tutorialStage;
					
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
		
		private Button signUp;
		private Button cancel;
		
		private Button aboutSinapsilink;
		
		// Labels
		private Label emailLabel;
		private Label passwordLabel;

		private Label registrationPassword;
		private Label registrationPasswordConfirmed;
		
		// TextFlow
		private TextFlow registerLabel;
				
		// Hyperlinks
		private Hyperlink registerLink;
		private Hyperlink forgotPasswordLink;
					
		// TextFields
		private TextField emailField;
		private PasswordField passwordField;
		
		private TextField registrationEmailField;
		private PasswordField registrationPasswordField;
		private PasswordField registrationPasswordFieldConfirmed;
		
		
		// Hbox
		private HBox hboxSignIn;	
		private HBox hboxSwitch;
		private HBox hboxRegistration;

		// Vbox
		private VBox vboxLogo;
		
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Sinapsi Login");
		this.primaryStage.setResizable(false);
		
		initLogin();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	
	/**
	 * Login layout launcher
	 */
	
	public void showErrorDialog() {
		
	}
	
	public void initLogin() {
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
		
		// Setting 'forgot password?' label (clickable) and color
		forgotPasswordLink = new Hyperlink("Forgot password?");
		forgotPasswordLink.setPadding(new Insets(2, 24, 2, 24));
		forgotPasswordLink.setTextFill(Color.web("#256581"));
		
		// Setting 'about label (clickable) and color
		aboutSinapsilink = new Button();
		aboutSinapsilink.setId("about-button");
		
		// Setting button text 
		signIn = new Button("Sign in");
		signIn.setId("login-button");

		// and hbox 
		hboxSignIn = new HBox(10);
		hboxSignIn.setAlignment(Pos.CENTER);
		hboxSignIn.getChildren().add(signIn);
		

		tutorialButton = new Button("?");
		tutorialButton.setId("tutorial-button");
		
		// Registration Buttons 
		signUp = new Button("Sign up");
			signUp.setId("register-button");
		cancel = new Button("Cancel");
			cancel.setId("cancel-button");
		
		// BorderPane Top	
		logoPane.setAlignment(Pos.TOP_CENTER);
		logoPane.setVgap(8);
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
		grid.add(forgotPasswordLink, 0, 7);
		grid.add(hboxSignIn, 0, 10);
		
		// BorderPane Bottom
		tutorialButtonPane.setAlignment(Pos.BOTTOM_RIGHT);
		tutorialButtonPane.setPadding(new Insets(25, 25, 25, 25));
		tutorialButtonPane.setHgap(10);
		tutorialButtonPane.add(aboutSinapsilink, 0, 1);
		tutorialButtonPane.add(tutorialButton, 1, 1);	
	
		
		/**
		 * 
		 * All the actions of the login layout
		 * 
		 */
		signIn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				controller = new LayoutController(LoginLayout.this.signIn, LoginLayout.this.primaryStage);
				controller.login(emailField.getText(), passwordField.getText());
			}
		});
		
		// Tutorial Button Handler	
		tutorialButton.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent event) {
				tutorialPane = new BorderPane();
				
				switchPagePrevious = new Button("Previous");
					switchPagePrevious.setPrefWidth(180);
					switchPagePrevious.setId("previous-button");
				switchPageNext = new Button("Next");
					switchPageNext.setPrefWidth(180);
					switchPageNext.setId("next-button");
					
				hboxSwitch = new HBox();
				hboxSwitch.setAlignment(Pos.CENTER);
				hboxSwitch.setPadding(new Insets(52,52,55,52));
				hboxSwitch.setSpacing(50);
				
				hboxSwitch.getChildren().addAll(switchPagePrevious, switchPageNext);
				tutorialPane.setBottom(hboxSwitch);
				
				FadeTransition thirdTransition = new FadeTransition(Duration.millis(800), tutorialPane);
				thirdTransition.setFromValue(0.0);
				thirdTransition.setToValue(1.0);
				thirdTransition.play();
				
				Scene tutorialScene = new Scene(tutorialPane,400,550);
				tutorialScene.getStylesheets().add("file:style/style.css");
				
				tutorialStage = new Stage();
				tutorialStage.setTitle("What is Sinapsi?");
				tutorialStage.setScene(tutorialScene);
				tutorialStage.setResizable(false);
				tutorialStage.show();
			}
		});
		
		// Register link handler
		registerLink.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				registrationEmailField = new TextField();
				registrationPasswordField = new PasswordField();
				registrationPasswordFieldConfirmed = new PasswordField();
				registrationPassword = new Label("New password");
				registrationPasswordConfirmed = new Label("Confirm password");
				
				
				
				hboxRegistration = new HBox(10);
				hboxRegistration.setSpacing(25);
				hboxRegistration.setAlignment(Pos.CENTER);
				hboxRegistration.getChildren().add(signUp);
				hboxRegistration.getChildren().add(cancel);
				
				grid.getChildren().clear();
				
				// Registration Pane
				grid.setAlignment(Pos.CENTER);
				grid.setHgap(15);
				grid.setVgap(10);
				grid.setPadding(new Insets(25, 25, 25, 25));
				
				grid.add(emailLabel, 0, 2);
				grid.add(registrationEmailField, 0, 3);
				grid.add(registrationPassword, 0, 4);
				grid.add(registrationPasswordField, 0, 5);
				grid.add(registrationPasswordConfirmed, 0, 6);
				grid.add(registrationPasswordFieldConfirmed, 0, 7);
				grid.add(hboxRegistration, 0, 9);	
				
				FadeTransition firstTransition = new FadeTransition(Duration.millis(500), grid);
				firstTransition.setFromValue(0.0);
				firstTransition.setToValue(1.0);
				firstTransition.play();
			}
		});
	
		
		// Forgot password link handler
		forgotPasswordLink.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getHostServices().showDocument("http://todaymade.com/blog/wp-content/uploads/2013/03/troll-face.png");
			}
		});
		
		
		// About link handler
		aboutSinapsilink.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getHostServices().showDocument("https://github.com/SinapsiProject");
			}
		});
		
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				grid.getChildren().clear();
				

				grid.setAlignment(Pos.CENTER);
				grid.setHgap(15);
				grid.setVgap(10);
				grid.setPadding(new Insets(25, 25, 25, 25));
				
				emailField.clear();
					
				grid.add(emailLabel, 0, 2);
				grid.add(emailField, 0, 3);
				grid.add(passwordLabel, 0, 4);
				grid.add(passwordField, 0, 5);
				grid.add(registerLabel, 0, 6);
				grid.add(forgotPasswordLink, 0, 7);
				grid.add(hboxSignIn, 0, 10);
				FadeTransition secondTransition = new FadeTransition(Duration.millis(500), grid);
				secondTransition.setFromValue(0.0);
				secondTransition.setToValue(1.0);
				secondTransition.play();
				
			}
		});
				
		
		root.setBottom(tutorialButtonPane);
		root.setTop(logoPane);
		root.setCenter(grid);
		
		FadeTransition rootTransition = new FadeTransition(Duration.millis(1300), root);
		rootTransition.setFromValue(0.0);
		rootTransition.setToValue(1.0);
		rootTransition.play();
		Scene loginScene = new Scene(root, 800, 600);
		loginScene.getStylesheets().add("file:style/login-style.css");
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				System.exit(0);
				controller.logout();
			};
		});
		primaryStage.setScene(loginScene);
		primaryStage.setTitle("Sinapsi Login");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}
