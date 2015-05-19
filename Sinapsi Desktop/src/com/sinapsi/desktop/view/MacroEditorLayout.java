package com.sinapsi.desktop.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class MacroEditorLayout extends Application {
	
	// Stages
	private Stage primaryStage;
	
	// Scenes
	private Scene mainScene;
	
	// Panes
	private SplitPane splitPane;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Sinapsi Macro Editor");
		primaryStage.setResizable(true);
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void initMacroEditor() {
		
	}
}
