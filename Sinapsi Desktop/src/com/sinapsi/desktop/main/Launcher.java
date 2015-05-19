/**
 * @author Marco Grillo
 */

package com.sinapsi.desktop.main;

import com.sinapsi.desktop.view.LoginLayout;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {	
	
	private Stage primaryStage;
	
	@Override
	public void start(Stage stage) {
		LoginLayout root = new LoginLayout();
		this.primaryStage = stage;
		root.start(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}	
	
	