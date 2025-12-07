package com.tmse.pizza.gui;

import com.tmse.pizza.models.*;
import com.tmse.pizza.storage.FileStorage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Order display window showing order details and totals
 */
public class OrderDisplayWindow {
    private Stage stage;
    private Order order;
    private User user;
    private List<OrderItem> cartItems;

    public OrderDisplayWindow(Stage parentStage, Order order, User user) {
        this.stage = parentStage; // Use the same stage instead of creating a new popup
        this.order = order;
        this.user = user;
        this.cartItems = new ArrayList<>();
    }
    
    public OrderDisplayWindow(Stage parentStage, Order order, User user, List<OrderItem> cartItems) {
        this.stage = parentStage; // Use the same stage instead of creating a new popup
        this.order = order;
        this.user = user;
        this.cartItems = cartItems != null ? new ArrayList<>(cartItems) : new ArrayList<>();
    }

    public void show() {
        // Validate order
        if (order == null) {
            showAlert("Error: Order is null");
            return;
        }
        
        if (order.getItems() == null || order.getItems().isEmpty()) {
            showAlert("Error: Order has no items");
            return;
        }
        
        stage.setTitle("Order Summary - TMSE Pizza");
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ffffff;");
        
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(30));
        centerBox.setStyle("-fx-background-color: #ffffff;");

        Label titleLabel = new Label("Order Summary");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        centerBox.getChildren().add(titleLabel);

        Label orderIdLabel = new Label("Order ID: " + (order.getOrderId() != null ? order.getOrderId() : "N/A"));
        orderIdLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        centerBox.getChildren().add(orderIdLabel);

        Separator separator1 = new Separator();
        centerBox.getChildren().add(separator1);

        Label itemsLabel = new Label("Items Ordered:");
        itemsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        centerBox.getChildren().add(itemsLabel);

        VBox itemsBox = new VBox(10);
        for (OrderItem item : order.getItems()) {
            HBox itemBox = new HBox(10);
            itemBox.setPadding(new Insets(5));

            String itemDetails = item.getName();
            if (item.getPizzaSize() != null) {
                itemDetails += " - " + item.getPizzaSize().getLabel();
            }
            if (item.getCrustType() != null) {
                itemDetails += " - " + item.getCrustType().getLabel();
            }
            if (item.getBeverageSize() != null) {
                itemDetails += " - " + item.getBeverageSize().getLabel();
            }
            if (item.getToppings() != null && !item.getToppings().isEmpty()) {
                itemDetails += " - Toppings: ";
                StringBuilder toppingsList = new StringBuilder();
                for (String toppingId : item.getToppings()) {
                    Topping topping = MenuData.getToppingById(toppingId);
                    if (topping != null) {
                        if (toppingsList.length() > 0) {
                            toppingsList.append(", ");
                        }
                        toppingsList.append(topping.getName());
                    }
                }
                if (toppingsList.length() > 0) {
                    itemDetails += toppingsList.toString();
                }
            }

            Label itemLabel = new Label(itemDetails);
            itemLabel.setWrapText(true);
            HBox.setHgrow(itemLabel, Priority.ALWAYS);

            Label priceLabel = new Label("$" + String.format("%.2f", item.getTotalPrice()));
            priceLabel.setStyle("-fx-font-weight: bold;");

            itemBox.getChildren().addAll(itemLabel, priceLabel);
            itemsBox.getChildren().add(itemBox);
        }

        ScrollPane scrollPane = new ScrollPane(itemsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);
        centerBox.getChildren().add(scrollPane);

        Separator separator2 = new Separator();
        centerBox.getChildren().add(separator2);

        VBox totalsBox = new VBox(10);
        totalsBox.setPadding(new Insets(10));

        HBox subtotalBox = new HBox();
        subtotalBox.setAlignment(Pos.CENTER_RIGHT);
        Label subtotalLabel = new Label("Subtotal:");
        Label subtotalValue = new Label("$" + String.format("%.2f", order.getSubtotal()));
        HBox.setHgrow(subtotalLabel, Priority.ALWAYS);
        subtotalBox.getChildren().addAll(subtotalLabel, subtotalValue);

        HBox taxBox = new HBox();
        taxBox.setAlignment(Pos.CENTER_RIGHT);
        Label taxLabel = new Label("Tax (8%):");
        Label taxValue = new Label("$" + String.format("%.2f", order.getTax()));
        HBox.setHgrow(taxLabel, Priority.ALWAYS);
        taxBox.getChildren().addAll(taxLabel, taxValue);

        Separator separator3 = new Separator();
        
        HBox totalBox = new HBox();
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        Label totalLabel = new Label("Total:");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label totalValue = new Label("$" + String.format("%.2f", order.getTotal()));
        totalValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
        HBox.setHgrow(totalLabel, Priority.ALWAYS);
        totalBox.getChildren().addAll(totalLabel, totalValue);

        totalsBox.getChildren().addAll(subtotalBox, taxBox, separator3, totalBox);
        centerBox.getChildren().add(totalsBox);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button confirmButton = new Button("Confirm Order");
        confirmButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 10 30; -fx-font-size: 14px;");
        confirmButton.setOnAction(e -> {
            try {
                // Ensure order status is set to pending
                order.setStatus("pending");
                // Ensure order date is set
                if (order.getOrderDate() == null) {
                    order.setOrderDate(new java.util.Date());
                }
                // Save the order
                FileStorage.saveOrder(order);
                
                // Clear cart and navigate back to landing page
                LandingWindow landingWindow = new LandingWindow(stage, new Order("ORD-" + System.currentTimeMillis(), "guest"), new ArrayList<>());
                landingWindow.show();
            } catch (IOException ex) {
                showAlert("Error saving order: " + ex.getMessage());
            }
        });

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #6b7280; -fx-text-fill: white; -fx-padding: 10 30; -fx-font-size: 14px;");
        backButton.setOnAction(e -> {
            // Navigate back to landing page
            LandingWindow landingWindow = new LandingWindow(stage, order, cartItems);
            landingWindow.show();
        });

        buttonBox.getChildren().addAll(backButton, confirmButton);
        centerBox.getChildren().add(buttonBox);
        
        // Wrap centerBox in ScrollPane for proper scrolling
        ScrollPane mainScroll = new ScrollPane(centerBox);
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("-fx-background-color: #ffffff;");
        root.setCenter(mainScroll);
        
        // Add header and footer for consistency
        CommonLayout.HeaderInfo headerInfo = CommonLayout.createHeader(stage, order, order.getItems(), () -> {
            // Navigate back to landing page when home is clicked
            LandingWindow landingWindow = new LandingWindow(stage, order, cartItems);
            landingWindow.show();
        }, "order");
        root.setTop(headerInfo.getHeaderContainer());
        
        VBox footerBox = CommonLayout.createFooter();
        root.setBottom(footerBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Full screen is already set at startup, no need to toggle
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

