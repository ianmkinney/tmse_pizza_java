package com.tmse.pizza.gui;

import com.tmse.pizza.models.*;
import com.tmse.pizza.storage.FileStorage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Order display window showing order details and totals
 */
public class OrderDisplayWindow {
    private Stage stage;
    private Order order;
    private User user;

    public OrderDisplayWindow(Stage parentStage, Order order, User user) {
        this.stage = new Stage();
        this.order = order;
        this.user = user;
        this.stage.initOwner(parentStage);
    }

    public void show() {
        stage.setTitle("Order Summary - TMSE Pizza");
        Stage parentStage = (Stage) stage.getOwner();
        if (parentStage != null) {
            double currentWidth = parentStage.getWidth() > 0 ? parentStage.getWidth() : 1400;
            double currentHeight = parentStage.getHeight() > 0 ? parentStage.getHeight() : 900;
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
        } else {
            stage.setWidth(1400);
            stage.setHeight(900);
        }
        stage.setResizable(true);

        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #ffffff;");

        Label titleLabel = new Label("Order Summary");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        root.getChildren().add(titleLabel);

        Label orderIdLabel = new Label("Order ID: " + order.getOrderId());
        orderIdLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        root.getChildren().add(orderIdLabel);

        Separator separator1 = new Separator();
        root.getChildren().add(separator1);

        Label itemsLabel = new Label("Items Ordered:");
        itemsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        root.getChildren().add(itemsLabel);

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
                for (String toppingId : item.getToppings()) {
                    Topping topping = MenuData.getToppingById(toppingId);
                    if (topping != null) {
                        itemDetails += topping.getName() + ", ";
                    }
                }
                itemDetails = itemDetails.substring(0, itemDetails.length() - 2);
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
        root.getChildren().add(scrollPane);

        Separator separator2 = new Separator();
        root.getChildren().add(separator2);

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
        root.getChildren().add(totalsBox);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button confirmButton = new Button("Confirm Order");
        confirmButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 10 30; -fx-font-size: 14px;");
        confirmButton.setOnAction(e -> {
            try {
                FileStorage.saveOrder(order);
                showAlert("Order confirmed! Order ID: " + order.getOrderId());
                stage.close();
            } catch (IOException ex) {
                showAlert("Error saving order: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        root.getChildren().add(buttonBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

