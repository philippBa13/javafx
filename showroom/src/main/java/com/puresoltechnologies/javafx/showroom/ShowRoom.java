package com.puresoltechnologies.javafx.showroom;

import java.io.IOException;

import com.puresoltechnologies.javafx.extensions.StatusBar;
import com.puresoltechnologies.javafx.extensions.menu.AboutMenuItem;
import com.puresoltechnologies.javafx.extensions.splash.SplashScreen;
import com.puresoltechnologies.javafx.perspectives.PerspectiveContainer;
import com.puresoltechnologies.javafx.perspectives.PerspectiveService;
import com.puresoltechnologies.javafx.perspectives.menu.PerspectiveMenu;
import com.puresoltechnologies.javafx.perspectives.menu.ShowPartMenuItem;
import com.puresoltechnologies.javafx.preferences.Preferences;
import com.puresoltechnologies.javafx.preferences.menu.PreferencesMenuItem;
import com.puresoltechnologies.javafx.reactive.ReactiveFX;
import com.puresoltechnologies.javafx.showroom.perspectives.StartPerspective;
import com.puresoltechnologies.javafx.tasks.TasksStatusBar;
import com.puresoltechnologies.javafx.utils.FXThreads;
import com.puresoltechnologies.javafx.utils.ResourceUtils;
import com.puresoltechnologies.javafx.workspaces.Workspace;
import com.puresoltechnologies.javafx.workspaces.menu.ExitApplicationMenuItem;
import com.puresoltechnologies.javafx.workspaces.menu.RestartApplicationMenuItem;
import com.puresoltechnologies.javafx.workspaces.menu.SwitchWorkspaceMenu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class ShowRoom extends Application {

    private PerspectiveContainer perspectiveContainer;
    private SplashScreen splashScreen;

    @Override
    public void start(Stage stage) throws Exception {
	Image splashImage = ResourceUtils.getImage(ShowRoom.class, "splash/splash.jpeg");
	splashScreen = new SplashScreen(stage, splashImage, applicationStage -> {
	    try {
		applicationStage.setTitle("Tool Shed");
		applicationStage.setResizable(true);
		applicationStage.centerOnScreen();

		Image chartUpColorSmall = ResourceUtils.getImage(this, "icons/FatCow_Icons16x16/setup_slide_show.png");
		Image chartUpColorBig = ResourceUtils.getImage(this, "icons/FatCow_Icons32x32/setup_slide_show.png");
		applicationStage.getIcons().addAll(chartUpColorSmall, chartUpColorBig);

		perspectiveContainer = PerspectiveService.getContainer();
		BorderPane root = new BorderPane();
		addMenu(applicationStage, root);
		root.setCenter(perspectiveContainer);
		StatusBar statusBar = new StatusBar();
		HBox stretch = new HBox();
		HBox.setHgrow(stretch, Priority.ALWAYS);
		statusBar.getChildren().addAll(stretch, new TasksStatusBar());
		root.setBottom(statusBar);

		PerspectiveService.openPerspective(new StartPerspective());

		Scene scene = new Scene(root, 1280, 960);
		applicationStage.setScene(scene);
		applicationStage.show();
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	});
	splashScreen.addTask(() -> System.out.println("Starting...\n" //
		+ "/  ___| |                   | ___ \\                     \n" //
		+ "\\ `--.| |__   _____      __ | |_/ /___   ___  _ __ ___  \n" //
		+ " `--. \\ '_ \\ / _ \\ \\ /\\ / / |    // _ \\ / _ \\| '_ ` _ \\ \n" //
		+ "/\\__/ / | | | (_) \\ V  V /  | |\\ \\ (_) | (_) | | | | | |\n" //
		+ "\\____/|_| |_|\\___/ \\_/\\_/   \\_| \\_\\___/ \\___/|_| |_| |_|\n" //
		+ "\n" //
		+ "(c) PureSol Technologies\n"));

	splashScreen.addTask(() -> {
	    Preferences.initialize();
	    return null;
	});
	splashScreen.addTask(() -> PerspectiveService.initialize());
	splashScreen.addTask(() -> ReactiveFX.initialize());

	splashScreen.startApplication();

    }

    private void addMenu(Stage stage, BorderPane root) {
	// File Menu
	SwitchWorkspaceMenu switchWorkspaceMenu = new SwitchWorkspaceMenu(stage);
	RestartApplicationMenuItem restartApplicationMenuItem = new RestartApplicationMenuItem(stage);
	ExitApplicationMenuItem exitApplicationMenuItem = new ExitApplicationMenuItem(stage);
	Menu fileMenu = new Menu("File");
	fileMenu.getItems().addAll(switchWorkspaceMenu, restartApplicationMenuItem, exitApplicationMenuItem);
	// Window Menu
	ShowPartMenuItem showViewItem = new ShowPartMenuItem();
	PreferencesMenuItem preferencesItem = new PreferencesMenuItem();
	Menu windowMenu = new Menu("Window");
	windowMenu.getItems().addAll(showViewItem, new PerspectiveMenu(), new SeparatorMenuItem(), preferencesItem);
	// Help Menu
	AboutMenuItem aboutItem = new AboutMenuItem();
	Menu helpMenu = new Menu("Help");
	helpMenu.getItems().addAll(aboutItem);
	// Menu Bar
	MenuBar menuBar = new MenuBar();
	menuBar.getMenus().addAll(fileMenu, windowMenu, helpMenu);
	root.setTop(menuBar);

    }

    @Override
    public void stop() {
	ReactiveFX.shutdown();
	PerspectiveService.shutdown();
	Preferences.shutdown();
	try {
	    FXThreads.shutdown();
	} catch (InterruptedException e) {
	    System.err.println("FXThreads were not cleanly shutdown.");
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) throws InterruptedException {
	Workspace.launchApplicationInWorkspace(ShowRoom.class, args);
    }
}