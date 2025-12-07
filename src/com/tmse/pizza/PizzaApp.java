package com.tmse.pizza;

import com.tmse.pizza.gui.LandingWindow;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main application class for TMSE Pizza Ordering System
 * Starts with landing page - no login required
 */
public class PizzaApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Set full screen once at startup and maintain throughout application
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setResizable(true);
        
        // Ensure full screen is maintained across scene changes
        primaryStage.fullScreenProperty().addListener((obs, wasFullScreen, isNowFullScreen) -> {
            if (!isNowFullScreen && primaryStage.isShowing()) {
                // Immediately restore full screen if it gets disabled
                javafx.application.Platform.runLater(() -> {
                    primaryStage.setFullScreen(true);
                    primaryStage.setFullScreenExitHint("");
                });
            }
        });
        
        LandingWindow landingWindow = new LandingWindow(primaryStage);
        landingWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

