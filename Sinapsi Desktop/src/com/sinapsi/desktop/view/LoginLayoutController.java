package com.sinapsi.desktop.view;

import com.sinapsi.desktop.main.Launcher;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class LoginLayoutController {
	
	// Some stuff for the login layout :)	
	@FXML
	private Image mainLogo;
	
	@FXML 
	private Label emailLabel;
	@FXML
	private Label passwordLabel;
	@FXML 
	private Label registerLabel;
	@FXML 
	private Label copyrightsLabel;
	
	@FXML
	private TextField emailTextField;
	@FXML
	private PasswordField passwordTextField;
	
	// Reference to the launcher
	private Launcher launcher;
	
	/**
	 * - Constructor -
	 * The Constructor is called before the initialize() method 
	 */
	public LoginLayoutController() {
		
	}
	
	/**
	 * Initializes the controller class. This method is automatically called 
	 * after the fxml has been loaded.
	 */
	@FXML 
	private void initialize() {
		
	}
	
	/**
	 * This is called by the main application to give a reference back to itself
	 * @param launcher
	 */
	public void setLauncher(Launcher launcher) {
		this.launcher = launcher;
	}
}
