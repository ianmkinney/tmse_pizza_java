// Alternative implementation using Maximized mode instead of Full Screen
// This allows resizing while still filling the screen

package com.tmse.pizza;

import com.tmse.pizza.gui.LandingWindow;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Alternative PizzaApp using MAXIMIZED mode instead of FULL SCREEN
 * This allows window resizing while still filling the screen
 */
public class PizzaAppMaximized extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Set window to maximized (fills screen but allows resizing)
        primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        
        // Ensure window stays maximized after scene changes
        primaryStage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (!isNowMaximized && primaryStage.isShowing()) {
                // Optionally restore maximized state
                javafx.application.Platform.runLater(() -> {
                    primaryStage.setMaximized(true);
                });
            }
        });
        
        // Set minimum size to ensure reasonable window size
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        
        LandingWindow landingWindow = new LandingWindow(primaryStage);
        landingWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

