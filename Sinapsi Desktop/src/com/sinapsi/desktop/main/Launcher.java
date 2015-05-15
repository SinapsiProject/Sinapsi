/**
 * @author Marco Grillo
 */

package com.sinapsi.desktop.main;


import java.io.IOException;

import com.sinapsi.desktop.view.LoginLayoutController;

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
	private Stage secondaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Sinapsi Login");
		this.primaryStage.setResizable(false);
			
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}	
	
	