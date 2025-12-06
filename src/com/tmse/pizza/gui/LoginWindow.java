package com.tmse.pizza.gui;

import com.tmse.pizza.models.Order;
import com.tmse.pizza.models.User;
import com.tmse.pizza.storage.FileStorage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Login window for user authentication
 */
public class LoginWindow {
    private Stage stage;
    private User currentUser;

    public LoginWindow(Stage primaryStage) {
        this.stage = primaryStage;
        try {
            FileStorage.initializeDefaultUsers();
        } catch (IOException e) {
            showError("Error initializing users: " + e.getMessage());
        }
    }

    public void show() {
        stage.setTitle("TMSE Pizza - Login");
        double currentWidth = stage.getWidth() > 0 ? stage.getWidth() : 1400;
        double currentHeight = stage.getHeight() > 0 ? stage.getHeight() : 900;
        stage.setWidth(currentWidth);
        stage.setHeight(currentHeight);
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ffffff;");
        
        CommonLayout.HeaderInfo headerInfo = CommonLayout.createHeader(stage, null, null, () -> {
            LandingWindow landingWindow = new LandingWindow(stage);
            landingWindow.show();
        }, "login");
        root.setTop(headerInfo.getHeaderContainer());

        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(30));
        centerBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Welcome to TMSE Pizza");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        CheckBox rememberCheckbox = new CheckBox("Keep me signed in");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        loginButton.setDefaultButton(true);

        Label demoLabel = new Label("Demo Accounts:");
        demoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Button customerDemoButton = new Button("Login as Customer");
        customerDemoButton.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-padding: 8 20;");
        customerDemoButton.setOnAction(e -> {
            usernameField.setText("customer");
            passwordField.setText("password123");
            performLogin("customer", "password123");
        });
        
        Button adminDemoButton = new Button("Login as Admin");
        adminDemoButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 8 20;");
        adminDemoButton.setOnAction(e -> {
            usernameField.setText("admin");
            passwordField.setText("admin123");
            performLogin("admin", "admin123");
        });
        
        Button driverDemoButton = new Button("Login as Driver");
        driverDemoButton.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 8 20;");
        driverDemoButton.setOnAction(e -> {
            usernameField.setText("driver");
            passwordField.setText("driver123");
            performLogin("driver", "driver123");
        });

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            performLogin(username, password);
        });
        
        HBox demoButtonsBox = new HBox(10);
        demoButtonsBox.setAlignment(Pos.CENTER);
        demoButtonsBox.getChildren().addAll(customerDemoButton, adminDemoButton, driverDemoButton);

        centerBox.getChildren().addAll(titleLabel, usernameLabel, usernameField, passwordLabel,
                passwordField, rememberCheckbox, loginButton, demoLabel, demoButtonsBox);
        
        root.setCenter(centerBox);
        
        VBox footerBox = CommonLayout.createFooter();
        root.setBottom(footerBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void performLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        try {
            User user = FileStorage.getUser(username);
            if (user != null && user.getPassword().equals(password)) {
                currentUser = user;
                openMenuWindow();
            } else {
                showError("Invalid username or password");
            }
        } catch (IOException ex) {
            showError("Error: " + ex.getMessage());
        }
    }
    
    private void openMenuWindow() {
        if (currentUser.getRole().equals("admin")) {
            AdminWindow adminWindow = new AdminWindow(stage, currentUser);
            adminWindow.show();
        } else if (currentUser.getRole().equals("driver")) {
            DriverWindow driverWindow = new DriverWindow(stage, currentUser);
            driverWindow.show();
        } else {
            // Customers should see the landing screen with proper styling
            Order customerOrder = new Order("ORD-" + System.currentTimeMillis(), currentUser.getUsername());
            LandingWindow landingWindow = new LandingWindow(stage, customerOrder, new java.util.ArrayList<>());
            landingWindow.show();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public User getCurrentUser() {
        return currentUser;
    }
}

