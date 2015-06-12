package com.sinapsi.desktop.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.GridPane;
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
	private GridPane descriptionPane;
	private FlowPane buttonPane;
	
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
	private ButtonBar firstButtonBar;
	private ButtonBar secondButtonBar;
	
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
	private HBox groupButtonBox;
	private HBox macroGroupButtonBox;
	private HBox macroButtonBox;
	private HBox helpButtonBox;
	
	
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
		descriptionPane = new GridPane();
		buttonPane = new FlowPane();
		
		tableView = new TableView();
		groupColumn = new TableColumn("Groups");
			groupColumn.setResizable(false);
			groupColumn.setId("table-column");
		macroColumn = new TableColumn("Macros");
			macroColumn.setResizable(false);
			groupColumn.setId("table-column");
			
		groupColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5));
		macroColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5));
		
		tableView.getColumns().addAll(groupColumn, macroColumn);
		
		
		helpButton = new Button("?");
			helpButton.setId("help-button-editor");
		tryButton = new Button();
		deleteAction = new Button();
		deleteGroup = new Button("X");
			deleteGroup.setId("delete-group-button");
		deleteMacro = new Button("X");
			deleteMacro.setId("delete-macro-button");
		newGroup = new Button("+");
			newGroup.setId("new-group-button");
		newMacro = new Button("+");
			newMacro.setId("new-macro-button");
		newAction = new Button();
		runMacro = new Button(">");
			runMacro.setId("run-button");
		stopMacro = new Button("[]");
			stopMacro.setId("stop-button");
		editButton = new Button("Edit");
			editButton.setId("edit-button");
		
		firstButtonBar = new ButtonBar();
		secondButtonBar = new ButtonBar();
		
		groupButtonBox = new HBox(15);
		groupButtonBox.setAlignment(Pos.CENTER_LEFT);
		groupButtonBox.setPadding(new Insets(5, 95, 5, 5));
			groupButtonBox.getChildren().addAll(newGroup,deleteGroup);
		
		macroGroupButtonBox = new HBox(15);
		macroGroupButtonBox.setAlignment(Pos.CENTER_RIGHT);
		macroGroupButtonBox.setPadding(new Insets(5, 230, 5, 5));
			macroGroupButtonBox.getChildren().addAll(newMacro,deleteMacro);
		
		macroButtonBox = new HBox(15);
		macroButtonBox.setAlignment(Pos.CENTER_RIGHT);
		macroButtonBox.setPadding(new Insets(5, 130, 5, 5));
			macroButtonBox.getChildren().addAll(runMacro,stopMacro,editButton);
			
		helpButtonBox = new HBox(15);
		helpButtonBox.setAlignment(Pos.CENTER_RIGHT);
		helpButtonBox.setPadding(new Insets(5, 5, 5, 5));
			helpButtonBox.getChildren().add(helpButton);
		
		
			
		macroDescription = new Label("Macro description");
		
		buttonPane.getChildren().addAll(groupButtonBox, macroGroupButtonBox, macroButtonBox, helpButtonBox);
		
		splitPane.setPrefWidth(350);
		splitPane.getItems().add(tableView);
		
		mainPane.setBottom(buttonPane);
		mainPane.setCenter(descriptionPane);
		mainPane.setLeft(splitPane);
		
		
		Scene editorScene = new Scene(mainPane, 800, 600);
		editorScene.getStylesheets().add("file:style/editor-style.css");
		primaryStage.setScene(editorScene);
		primaryStage.setTitle("Sinapsi Macro Editor");
		primaryStage.setResizable(true);
		primaryStage.setMinHeight(600);
		primaryStage.setMinWidth(800);
		primaryStage.show();
		
	}
}
