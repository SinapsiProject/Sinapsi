/**
 * @author Marco Grillo
 */

package com.sinapsi.desktop.main;

import com.sinapsi.desktop.controller.RootAccess;
import com.sinapsi.desktop.service.BackgroundService;
import com.sinapsi.desktop.view.LoginLayout;

import java.awt.AWTException;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.MenuItem;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import retrofit.http.GET;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Launcher extends Application {	

	public static BackgroundService bgService;

	// The STAGE!
	private Stage primaryStage;

	// Tray stuff
	private SystemTray tray;
	private TrayIcon trayIcon;

	// Boolean for message show
	private boolean firstTime;

	// GUI stuff
	private MenuItem showItem;
	private MenuItem closeItem;
	private PopupMenu popUpMenu;

	// Images
	private Image icon;


	@Override
	public void start(Stage stage) {
		Thread t = new Thread(bgService);
		t.start();
		createTrayIcon(stage);
		LoginLayout root = new LoginLayout();
		this.primaryStage = stage;
		this.primaryStage.setResizable(false);
		root.start(primaryStage);
		try {
			bgService = new BackgroundService(RootAccess.getPasswdForRoot());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	// Creating TrayIcon while launching
	public void createTrayIcon(final Stage stage) {
		if(SystemTray.isSupported()) {
			// Get the SystemTray instance
			String path = "res/rsz_blue_cog.png";
			tray = SystemTray.getSystemTray();
			// Loading image
			icon = null;
			try {
				icon = ImageIO.read(new File(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				hide(stage);	
			}
		});

		// ActionListener
		final ActionListener closeListener = new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.exit(0);
			}
		};

		ActionListener showListener = new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						primaryStage.show();
					}
				});
			}
		};

		popUpMenu = new PopupMenu();
		showItem = new MenuItem("Show");
		closeItem = new MenuItem("Close");

		showItem.addActionListener(showListener);
		popUpMenu.add(showItem);

		closeItem.addActionListener(closeListener);
		popUpMenu.add(closeItem);

		trayIcon = new TrayIcon(icon, "Sinapsi", popUpMenu);
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("Sinapsi");
		trayIcon.addActionListener(showListener);

		try {
			tray.add(trayIcon);
		} catch(AWTException e) {
			e.printStackTrace();
		}
	}

	// Hiding 
	public void hide(final Stage stage) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if(SystemTray.isSupported()) {
					stage.hide();
					minimizedMessage();
				} else {
					System.exit(0);
				}
			}
		});
	}

	// Minimized window message
	public void minimizedMessage() {
		if (firstTime) {
			trayIcon.displayMessage("Sinapsi",
					"Sinapsi is now minimized!.",
					TrayIcon.MessageType.INFO);
			firstTime = false;
		}
	}
}	

