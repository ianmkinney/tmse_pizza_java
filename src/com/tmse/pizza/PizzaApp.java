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
        LandingWindow landingWindow = new LandingWindow(primaryStage);
        landingWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

