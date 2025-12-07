package com.tmse.pizza.gui;

import com.tmse.pizza.models.Order;
import com.tmse.pizza.models.User;
import com.tmse.pizza.storage.FileStorage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

// Login window for user authentication
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
        
        Button createAccountButton = new Button("Create Account");
        createAccountButton.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        createAccountButton.setOnAction(e -> showCreateAccountDialog());
        
        HBox demoButtonsBox = new HBox(10);
        demoButtonsBox.setAlignment(Pos.CENTER);
        demoButtonsBox.getChildren().addAll(customerDemoButton, adminDemoButton, driverDemoButton);

        centerBox.getChildren().addAll(titleLabel, usernameLabel, usernameField, passwordLabel,
                passwordField, rememberCheckbox, loginButton, createAccountButton, demoLabel, demoButtonsBox);
        
        root.setCenter(centerBox);
        
        VBox footerBox = CommonLayout.createFooter(stage, null, null);
        root.setBottom(footerBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Full screen is already set at startup, no need to toggle
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

    private void showCreateAccountDialog() {
        // Create a dialog for account creation
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Create Account");
        dialog.setHeaderText("Create a new TMSE Pizza account");
        
        // Set the button types
        ButtonType createButtonType = new ButtonType("Create Account", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        // Create the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("Enter username");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(newUsernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Enable/Disable create button depending on whether fields are filled
        Button createButton = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);
        
        // Validation
        newUsernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            createButton.setDisable(newValue.trim().isEmpty() || 
                                   newPasswordField.getText().isEmpty() || 
                                   confirmPasswordField.getText().isEmpty());
        });
        
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            createButton.setDisable(newValue.isEmpty() || 
                                   newUsernameField.getText().trim().isEmpty() || 
                                   confirmPasswordField.getText().isEmpty());
        });
        
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            createButton.setDisable(newValue.isEmpty() || 
                                   newUsernameField.getText().trim().isEmpty() || 
                                   newPasswordField.getText().isEmpty());
        });
        
        // Request focus on username field
        Platform.runLater(() -> newUsernameField.requestFocus());
        
        // Convert the result to a User when the create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String username = newUsernameField.getText().trim();
                String password = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                
                // Validate passwords match
                if (!password.equals(confirmPassword)) {
                    showError("Passwords do not match");
                    return null;
                }
                
                // Validate password length
                if (password.length() < 6) {
                    showError("Password must be at least 6 characters long");
                    return null;
                }
                
                // Validate username
                if (username.isEmpty()) {
                    showError("Username cannot be empty");
                    return null;
                }
                
                // Check if username already exists
                try {
                    User existingUser = FileStorage.getUser(username);
                    if (existingUser != null) {
                        showError("Username already exists. Please choose a different username.");
                        return null;
                    }
                } catch (IOException e) {
                    showError("Error checking username: " + e.getMessage());
                    return null;
                }
                
                // Create new user with customer role
                return new User(username, password, "customer");
            }
            return null;
        });
        
        // Process the result
        java.util.Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            try {
                FileStorage.saveUser(user);
                showSuccess("Account created successfully! You can now login with your new account.");
                // Optionally auto-login the new user
                currentUser = user;
                openMenuWindow();
            } catch (IOException e) {
                showError("Error creating account: " + e.getMessage());
            }
        });
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public User getCurrentUser() {
        return currentUser;
    }
}

