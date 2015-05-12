/**
 * @author Marco Grillo
 */

package com.sinapsi.desktop.view;

import com.sinapsi.desktop.main.Launcher;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class LoginLayoutController {
	
	// Some fields	
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField;
	
	// Some labels
	@FXML
	private Label emailLabel;
	@FXML
	private Label passwordLabel;
	@FXML
	private Label registerLabel;
	@FXML 
	private Label aboutLabel;
	
	// A button
	@FXML
	private Button signInButton;
	
	
	// Constructor
	public LoginLayoutController() {
		
	}
	
	public void initialize() {
		
	}
	
	
}
