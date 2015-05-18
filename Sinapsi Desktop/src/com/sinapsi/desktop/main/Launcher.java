/**
 * @author Marco Grillo
 */

package com.sinapsi.desktop.main;

import com.sinapsi.desktop.implementation.RootLayoutImplementation;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {	
	
	private Stage primaryStage;
	
	@Override
	public void start(Stage stage) {
		RootLayoutImplementation root = new RootLayoutImplementation();
		this.primaryStage = stage;
		root.start(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}	
	
	