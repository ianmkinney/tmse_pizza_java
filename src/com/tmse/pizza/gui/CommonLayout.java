package com.tmse.pizza.gui;

import com.tmse.pizza.models.Order;
import com.tmse.pizza.models.OrderItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Common header and footer layout for all pages
public class CommonLayout {
    
    // Header info containing the header container and cart button reference
    public static class HeaderInfo {
        private VBox headerContainer;
        private Button cartButton;
        List<OrderItem> cartItems; // Package-private so cart button action can access it
        
        public HeaderInfo(VBox headerContainer, Button cartButton, List<OrderItem> cartItems) {
            this.headerContainer = headerContainer;
            this.cartButton = cartButton;
            this.cartItems = cartItems;
        }
        
        public VBox getHeaderContainer() {
            return headerContainer;
        }
        
        public Button getCartButton() {
            return cartButton;
        }
        
        // Update the cart button text with current cart total
        public void updateCartButton() {
            if (cartButton != null) {
                double cartTotal = cartItems != null ? cartItems.stream().mapToDouble(OrderItem::getTotalPrice).sum() : 0.0;
                cartButton.setText("Cart: $" + String.format("%.2f", cartTotal));
            }
        }
        
        // Update the cart items list and refresh the button
        public void updateCartItems(List<OrderItem> newCartItems) {
            this.cartItems = newCartItems;
            updateCartButton();
        }
    }
    
    public static VBox createHeader(Stage stage, Order currentOrder, List<OrderItem> cartItems, Runnable backAction) {
        return createHeader(stage, currentOrder, cartItems, backAction, null).getHeaderContainer();
    }
    
    public static HeaderInfo createHeader(Stage stage, Order currentOrder, List<OrderItem> cartItems, Runnable backAction, String currentPage) {
        VBox headerContainer = new VBox();
        
        HBox headerBox = new HBox(20);
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #dc2626, #f59e0b); -fx-padding: 15 20;");
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        if (backAction != null) {
            Button backButton = new Button("← Back");
            backButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 1; -fx-border-radius: 3; -fx-padding: 5 10;");
            backButton.setOnAction(e -> backAction.run());
            headerBox.getChildren().add(backButton);
        }
        
        Label headerLabel = new Label("🥷🐢🍕 TMSE Pizza");
        headerLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");
        headerBox.getChildren().add(headerLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerBox.getChildren().add(spacer);

        final Order finalOrder = currentOrder;
        final List<OrderItem> finalCartItems = cartItems;
        
        Button menuButton = new Button("Menu");
        String menuStyle = "menu".equals(currentPage) 
            ? "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent; -fx-underline: true;"
            : "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent;";
        menuButton.setStyle(menuStyle);
        menuButton.setOnAction(e -> {
            LandingWindow landingWindow = new LandingWindow(stage, finalOrder, finalCartItems);
            landingWindow.show();
        });
        
        Button builderButton = new Button("Custom Pizza");
        String builderStyle = "custom-pizza".equals(currentPage)
            ? "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent; -fx-underline: true;"
            : "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent;";
        builderButton.setStyle(builderStyle);
        builderButton.setOnAction(e -> {
            Order orderToUse = finalOrder != null ? finalOrder : new Order("ORD-" + System.currentTimeMillis(), "guest");
            List<OrderItem> itemsToUse = finalCartItems != null ? finalCartItems : new java.util.ArrayList<>();
            PizzaBuilderWindow builderWindow = new PizzaBuilderWindow(stage, orderToUse, itemsToUse);
            builderWindow.show();
        });
        
        Button aboutButton = new Button("About Us");
        String aboutStyle = "about-us".equals(currentPage)
            ? "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent; -fx-underline: true;"
            : "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent;";
        aboutButton.setStyle(aboutStyle);
        aboutButton.setOnAction(e -> {
            AboutUsWindow aboutWindow = new AboutUsWindow(stage, finalOrder, finalCartItems);
            aboutWindow.show();
        });
        
        Button reviewsButton = new Button("Reviews");
        String reviewsStyle = "reviews".equals(currentPage)
            ? "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent; -fx-underline: true;"
            : "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent;";
        reviewsButton.setStyle(reviewsStyle);
        reviewsButton.setOnAction(e -> {
            ReviewsWindow reviewsWindow = new ReviewsWindow(stage, finalOrder, finalCartItems);
            reviewsWindow.show();
        });
        
        Button loginButton = new Button("Login");
        String loginStyle = "login".equals(currentPage)
            ? "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent; -fx-underline: true;"
            : "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent;";
        loginButton.setStyle(loginStyle);
        loginButton.setOnAction(e -> {
            LoginWindow loginWindow = new LoginWindow(stage);
            loginWindow.show();
        });

        Button cartButton = new Button();
        String cartStyle = "cart".equals(currentPage)
            ? "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: rgba(255,255,255,0.3); -fx-padding: 8 15; -fx-background-radius: 5; -fx-underline: true;"
            : "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: rgba(255,255,255,0.2); -fx-padding: 8 15; -fx-background-radius: 5;";
        cartButton.setStyle(cartStyle);
        
        double cartTotal = cartItems != null ? cartItems.stream().mapToDouble(OrderItem::getTotalPrice).sum() : 0.0;
        cartButton.setText("Cart: $" + String.format("%.2f", cartTotal));
        
        headerBox.getChildren().addAll(menuButton, builderButton, aboutButton, reviewsButton, loginButton, cartButton);
        
        HBox navBox = new HBox();
        navBox.setPadding(new Insets(0));
        navBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 0 0 2 0;");
        navBox.setPrefHeight(0);
        
        headerContainer.getChildren().addAll(headerBox, navBox);
        
        // Create HeaderInfo first
        HeaderInfo headerInfo = new HeaderInfo(headerContainer, cartButton, cartItems);
        
        // Set cart button action to use current cart items from HeaderInfo
        cartButton.setOnAction(e -> {
            List<OrderItem> currentCartItems = headerInfo.cartItems != null ? headerInfo.cartItems : new java.util.ArrayList<>();
            Order currentOrderForCart = finalOrder != null ? finalOrder : new Order("ORD-" + System.currentTimeMillis(), "guest");
            CartWindow cartWindow = new CartWindow(stage, currentOrderForCart, currentCartItems, () -> {});
            cartWindow.show();
        });
        
        return headerInfo;
    }
    
    public static VBox createFooter(Stage stage, Order currentOrder, List<OrderItem> cartItems) {
        VBox footerBox = new VBox(10);
        footerBox.setPadding(new Insets(20));
        footerBox.setStyle("-fx-background-color: #1f2937; -fx-alignment: center;");
        
        Label footerLabel = new Label("2025 TMSE Pizza");
        footerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        HBox linksBox = new HBox(20);
        linksBox.setAlignment(Pos.CENTER);
        
        final Order finalOrder = currentOrder != null ? currentOrder : new Order("ORD-" + System.currentTimeMillis(), "guest");
        final List<OrderItem> finalCartItems = cartItems != null ? cartItems : new java.util.ArrayList<>();
        
        Label aboutLink = new Label("About Us");
        aboutLink.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 12px; -fx-cursor: hand;");
        aboutLink.setOnMouseClicked(e -> {
            AboutUsWindow aboutWindow = new AboutUsWindow(stage, finalOrder, finalCartItems);
            aboutWindow.show();
        });
        
        Label reviewsLink = new Label("Reviews");
        reviewsLink.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 12px; -fx-cursor: hand;");
        reviewsLink.setOnMouseClicked(e -> {
            ReviewsWindow reviewsWindow = new ReviewsWindow(stage, finalOrder, finalCartItems);
            reviewsWindow.show();
        });
        
        Label mapLink = new Label("Map");
        mapLink.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 12px; -fx-cursor: hand;");
        mapLink.setOnMouseClicked(e -> {
            try {
                URI uri = new URI("https://www.google.com/maps/search/Splinter's+Lair");
                Desktop.getDesktop().browse(uri);
            } catch (Exception ex) {
                // If Desktop.browse() fails, try alternative method
                try {
                    Runtime.getRuntime().exec(new String[]{"open", "https://www.google.com/maps/search/Splinter's+Lair"});
                } catch (Exception ex2) {
                    System.err.println("Failed to open browser: " + ex2.getMessage());
                }
            }
        });
        
        linksBox.getChildren().addAll(aboutLink, reviewsLink, mapLink);
        
        // Create pizza slice divider
        HBox dividerBox = createPizzaSliceDivider();
        
        footerBox.getChildren().addAll(footerLabel, dividerBox, linksBox);
        
        return footerBox;
    }
    
    // Create two rectangular pizza slices with pepperonis as divider
    private static HBox createPizzaSliceDivider() {
        HBox dividerBox = new HBox(5);
        dividerBox.setAlignment(Pos.CENTER);
        dividerBox.setPadding(new Insets(10, 0, 10, 0));
        
        // Create two pizza slices
        StackPane slice1 = createPizzaSlice("slice1");
        StackPane slice2 = createPizzaSlice("slice2");
        
        dividerBox.getChildren().addAll(slice1, slice2);
        
        return dividerBox;
    }
    
    // Create a single rectangular pizza slice with random pepperonis
    private static StackPane createPizzaSlice(String seed) {
        StackPane slicePane = new StackPane();
        slicePane.setPrefWidth(30);
        slicePane.setPrefHeight(60);
        
        // Crust (outer rectangle with rounded corners)
        Rectangle crust = new Rectangle(30, 60);
        crust.setFill(Color.rgb(217, 119, 6));
        crust.setStroke(Color.rgb(180, 83, 9));
        crust.setStrokeWidth(2);
        crust.setArcWidth(8);
        crust.setArcHeight(8);
        
        // Sauce/base (inner rectangle)
        Rectangle sauce = new Rectangle(24, 54);
        sauce.setFill(Color.rgb(220, 38, 38));
        sauce.setStroke(Color.rgb(185, 28, 28));
        sauce.setStrokeWidth(1.5);
        sauce.setArcWidth(6);
        sauce.setArcHeight(6);
        
        // Cheese layer
        Rectangle cheese = new Rectangle(22, 52);
        cheese.setFill(Color.rgb(254, 243, 199));
        cheese.setStroke(Color.rgb(251, 191, 36));
        cheese.setStrokeWidth(1);
        cheese.setArcWidth(5);
        cheese.setArcHeight(5);
        
        slicePane.getChildren().addAll(crust, sauce, cheese);
        
        // Add random pepperonis
        Random random = new Random(seed.hashCode());
        int pepperoniCount = 4 + random.nextInt(6); // 4-9 pepperonis
        List<Double> usedX = new ArrayList<>();
        List<Double> usedY = new ArrayList<>();
        
        for (int i = 0; i < pepperoniCount; i++) {
            double x = -10 + random.nextDouble() * 20; // -10 to 10
            double y = -25 + random.nextDouble() * 50; // -25 to 25
            
            // Check if too close to other pepperonis
            boolean tooClose = false;
            for (int j = 0; j < usedX.size(); j++) {
                double dist = Math.sqrt(Math.pow(x - usedX.get(j), 2) + Math.pow(y - usedY.get(j), 2));
                if (dist < 6) {
                    tooClose = true;
                    break;
                }
            }
            
            if (!tooClose) {
                usedX.add(x);
                usedY.add(y);
                
                Circle pepperoni = new Circle(2.5 + random.nextDouble() * 1.5); // 2.5-4 radius
                pepperoni.setFill(Color.rgb(153, 27, 27));
                pepperoni.setStroke(Color.rgb(120, 20, 20));
                pepperoni.setStrokeWidth(0.5);
                pepperoni.setTranslateX(x);
                pepperoni.setTranslateY(y);
                
                slicePane.getChildren().add(pepperoni);
            } else {
                i--; // Try again
            }
        }
        
        return slicePane;
    }
}

