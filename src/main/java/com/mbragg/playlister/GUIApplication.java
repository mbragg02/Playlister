package com.mbragg.playlister;

import com.mbragg.playlister.configurations.ApplicationConfiguration;
import com.mbragg.playlister.configurations.GUIApplicationConfiguration;
import com.mbragg.playlister.controllers.viewControllers.ViewController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Class for launching the GUI playlist generator application.
 * <p>
 * Configures the Spring framework context for the application, initializes and launches the GUI view.
 * Extends JavaFX Application to launch GUI.
 *
 * @author Michael Bragg
 */
public class GUIApplication extends Application {

    private static final int WIDTH = 640;
    public static final int HEIGHT = 700;
    public static final String WINDOW_TITLE = "Playlist generator V3";

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called from launch(args) in Main method.
     *
     * @param stage Stage. The JavaFX initial stage from Application.
     * @throws Exception Inherited from JavaFX Application
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Configure the application's context (Spring framework)
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext(ApplicationConfiguration.class, GUIApplicationConfiguration.class);

        ViewController viewController = context.getBean(ViewController.class);

        // Set the JavaFX scene / stage and initialize the view controller
        Scene scene = new Scene((Parent) viewController.getView(), WIDTH, HEIGHT);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle(WINDOW_TITLE);
        viewController.initializeViewController(stage);

        stage.show();
    }
}
