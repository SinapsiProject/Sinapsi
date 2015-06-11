package com.sinapsi.desktop.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class MacroEditorLayout extends Application {
	
	// Stages
	private Stage primaryStage;
	
	// Scenes
	private Scene mainScene;
	
	// Panes
	private BorderPane mainPane;
	private SplitPane splitPane;
	private StackPane descriptionPane;
	
	// Buttons
	private Button editButton;
	private Button tryButton;
	private Button newMacro;
	private Button newGroup;
	private Button deleteMacro;
	private Button deleteGroup;
	private Button runMacro;
	private Button stopMacro;
	private Button newAction;
	private Button deleteAction;
	private Button helpButton;	
	
	// ButtonBar
	private ButtonBar buttonBar;
	
	// Labels
	private Label macroDescription;
	private Label macroName;
	private Label actionName;
	
	
	// Textfields
	private TextField macroNameField;
	
	// TableView & Columns
	private TableView tableView;
	private TableColumn groupColumn;
	private TableColumn macroColumn;
	
	// Hbox
	private HBox buttonBox;
	
	
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
		mainPane = new BorderPane();
		splitPane = new SplitPane();
		descriptionPane = new StackPane();
		
		tableView = new TableView();
		groupColumn = new TableColumn("Groups");
			groupColumn.setResizable(false);
		macroColumn = new TableColumn("Macros");
			macroColumn.setResizable(false);
			
		groupColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5));
		macroColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5));
		
		tableView.getColumns().addAll(groupColumn, macroColumn);
		
		
		helpButton = new Button();
		tryButton = new Button();
		deleteAction = new Button();
		deleteGroup = new Button();
		deleteMacro = new Button();
		newGroup = new Button();
		newMacro = new Button();
		newAction = new Button();
		runMacro = new Button();
		stopMacro = new Button();
		editButton = new Button("Edit");
		
		buttonBar =  new ButtonBar();
		buttonBar.getButtons().addAll(newGroup, deleteGroup, newMacro, deleteMacro,
									  runMacro, stopMacro, editButton, tryButton, helpButton);
		buttonBox = new HBox();
		buttonBox.setPadding(new Insets(5, 0, 5, 0));
		//buttonBox.getChildren().add(buttonBar);
		
		
		macroDescription = new Label("Macro description");
		
		splitPane.setPrefWidth(350);
		splitPane.getItems().add(tableView);
		
		
		mainPane.setCenter(descriptionPane);
		mainPane.setLeft(splitPane);
		mainPane.setBottom(buttonBox);
		
		Scene editorScene = new Scene(mainPane, 800, 600);
		primaryStage.setScene(editorScene);
		primaryStage.setTitle("Sinapsi Macro Editor");
		primaryStage.setResizable(true);
		primaryStage.setMinHeight(600);
		primaryStage.setMinWidth(800);
		primaryStage.show();
		
	}
}
