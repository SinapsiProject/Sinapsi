package com.sinapsi.desktop.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MacroEditorLayout extends Application {
	
	// Stages
	private Stage primaryStage;
	
	// Scenes
	private Scene mainScene;
	
	// Panes
	private BorderPane mainBorder;
	private SplitPane splitPane;
	private StackPane descriptionPane;
	private TitledPane groupPane;
	private TitledPane macroPane;
	
	// Buttons
	private Button editButton;
	private Button tryButton;
	private Button newMacro;
	private Button newGroup;
	private Button deleteMacro;
	private Button deleteGroup;
	private Button runMacro;
	private Button newAction;
	private Button deleteAction;
	private Button helpButton;	
	
	// ButtonBar
	private ButtonBar buttonBar;
	
	// Labels
	private Label macroDescription;
	
	// Textfields
	private TextField macroNameField;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Sinapsi Macro Editor");
		primaryStage.setResizable(true);
		initMacroEditor();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void initMacroEditor() {
		
		// Setting main pane
		mainBorder = new BorderPane();
		splitPane = new SplitPane();
		groupPane = new TitledPane("Groups", null);
		macroPane = new TitledPane("Macros", null);
		descriptionPane = new StackPane();
		
		buttonBar = new ButtonBar();
		
		mainBorder.setLeft(splitPane);
		mainBorder.setRight(descriptionPane);
		mainBorder.setBottom(buttonBar);
		
		
		
		Scene editorScene = new Scene(mainBorder, 800, 600);
		primaryStage.setScene(editorScene);
		primaryStage.setTitle("Sinapsi Macro Editor");
		primaryStage.setResizable(true);
		primaryStage.show();
		
	}
}
