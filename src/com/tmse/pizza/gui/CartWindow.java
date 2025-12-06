package com.tmse.pizza.gui;

import com.tmse.pizza.models.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Cart window matching hand-drawn design
 */
public class CartWindow {
    private Stage stage;
    private Order currentOrder;
    private List<OrderItem> cartItems;
    private Runnable onCartUpdated;
    private CommonLayout.HeaderInfo headerInfo;

    public CartWindow(Stage primaryStage, Order order, List<OrderItem> items, Runnable onCartUpdated) {
        this.stage = primaryStage;
        this.currentOrder = order;
        this.cartItems = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.onCartUpdated = onCartUpdated;
    }

    public void show() {
        stage.setTitle("TMSE Pizza - Cart");
        double currentWidth = stage.getWidth() > 0 ? stage.getWidth() : 1400;
        double currentHeight = stage.getHeight() > 0 ? stage.getHeight() : 900;
        stage.setWidth(currentWidth);
        stage.setHeight(currentHeight);
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ffffff;");

        CommonLayout.HeaderInfo headerInfo = CommonLayout.createHeader(stage, currentOrder, cartItems, () -> {
            LandingWindow landingWindow = new LandingWindow(stage, currentOrder, cartItems);
            landingWindow.show();
        }, "cart");
        root.setTop(headerInfo.getHeaderContainer());

        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(30));
        centerBox.setStyle("-fx-background-color: #f9fafb;");

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER);
        Label cartTitle = new Label("Cart");
        cartTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        titleBox.getChildren().add(cartTitle);
        centerBox.getChildren().add(titleBox);

        Label itemsLabel = new Label("Items");
        itemsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-alignment: center-right;");
        itemsLabel.setAlignment(Pos.CENTER_RIGHT);
        centerBox.getChildren().add(itemsLabel);

        VBox itemsBox = new VBox(15);
        itemsBox.setPadding(new Insets(10));

        List<OrderItem> itemsCopy = new ArrayList<>(cartItems);
        for (int i = 0; i < itemsCopy.size(); i++) {
            final int index = i;
            final OrderItem item = itemsCopy.get(i);
            HBox itemBox = createItemRow(item, index);
            itemsBox.getChildren().add(itemBox);
        }

        ScrollPane itemsScroll = new ScrollPane(itemsBox);
        itemsScroll.setFitToWidth(true);
        itemsScroll.setPrefHeight(300);
        centerBox.getChildren().add(itemsScroll);

        Separator separator = new Separator();
        centerBox.getChildren().add(separator);

        VBox summaryBox = new VBox(10);
        summaryBox.setPadding(new Insets(20));
        summaryBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 5;");

        double subtotal = cartItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
        double tax = subtotal * 0.08;
        double total = subtotal + tax;

        HBox subtotalRow = new HBox();
        subtotalRow.setAlignment(Pos.CENTER_RIGHT);
        Label subtotalLabel = new Label("Subtotal:");
        Label subtotalValue = new Label("$" + String.format("%.2f", subtotal));
        subtotalValue.setStyle("-fx-font-weight: bold;");
        HBox.setHgrow(subtotalLabel, Priority.ALWAYS);
        subtotalRow.getChildren().addAll(subtotalLabel, subtotalValue);

        HBox taxRow = new HBox();
        taxRow.setAlignment(Pos.CENTER_RIGHT);
        Label taxLabel = new Label("Tax (8%):");
        Label taxValue = new Label("$" + String.format("%.2f", tax));
        taxValue.setStyle("-fx-font-weight: bold;");
        HBox.setHgrow(taxLabel, Priority.ALWAYS);
        taxRow.getChildren().addAll(taxLabel, taxValue);

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_RIGHT);
        Label totalLabel = new Label("Total:");
        totalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label totalValue = new Label("$" + String.format("%.2f", total));
        totalValue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
        HBox.setHgrow(totalLabel, Priority.ALWAYS);
        totalRow.getChildren().addAll(totalLabel, totalValue);

        summaryBox.getChildren().addAll(subtotalRow, taxRow, totalRow);
        centerBox.getChildren().add(summaryBox);

        Label deliveryLabel = new Label("Delivery and Payment Info");
        deliveryLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        centerBox.getChildren().add(deliveryLabel);

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(20));
        infoGrid.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 5;");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        infoGrid.add(nameLabel, 0, 0);
        infoGrid.add(nameField, 1, 0);

        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();
        infoGrid.add(addressLabel, 0, 1);
        infoGrid.add(addressField, 1, 1);

        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();
        infoGrid.add(phoneLabel, 0, 2);
        infoGrid.add(phoneField, 1, 2);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        infoGrid.add(emailLabel, 0, 3);
        infoGrid.add(emailField, 1, 3);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        infoGrid.getColumnConstraints().addAll(col1, col2);

        centerBox.getChildren().add(infoGrid);

        HBox deliveryMethodBox = new HBox(20);
        deliveryMethodBox.setPadding(new Insets(20));
        deliveryMethodBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 5;");

        Label deliveryMethodLabel = new Label("Delivery Method:");
        deliveryMethodLabel.setStyle("-fx-font-weight: bold;");
        ToggleGroup deliveryGroup = new ToggleGroup();
        RadioButton pickupRadio = new RadioButton("Pick-up");
        pickupRadio.setToggleGroup(deliveryGroup);
        pickupRadio.setSelected(true);
        RadioButton deliveryRadio = new RadioButton("Delivery");
        deliveryRadio.setToggleGroup(deliveryGroup);

        deliveryMethodBox.getChildren().addAll(deliveryMethodLabel, pickupRadio, deliveryRadio);
        centerBox.getChildren().add(deliveryMethodBox);

        // Demo Application Warning
        VBox demoWarningBox = new VBox(10);
        demoWarningBox.setPadding(new Insets(15));
        demoWarningBox.setStyle("-fx-background-color: #fef3c7; -fx-border-color: #f59e0b; -fx-border-width: 2; -fx-border-radius: 5;");
        
        Label demoWarningTitle = new Label("DEMO APPLICATION - NO REAL PAYMENTS");
        demoWarningTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #92400e;");
        
        Label demoWarningText = new Label("This is a demonstration application. DO NOT enter any real credit card numbers or payment information. All payment fields are optional and no actual payment processing occurs.");
        demoWarningText.setStyle("-fx-font-size: 12px; -fx-text-fill: #78350f; -fx-wrap-text: true;");
        demoWarningText.setWrapText(true);
        
        demoWarningBox.getChildren().addAll(demoWarningTitle, demoWarningText);
        centerBox.getChildren().add(demoWarningBox);

        HBox paymentMethodBox = new HBox(20);
        paymentMethodBox.setPadding(new Insets(20));
        paymentMethodBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 5;");

        Label paymentMethodLabel = new Label("Payment Details:");
        paymentMethodLabel.setStyle("-fx-font-weight: bold;");
        ToggleGroup paymentGroup = new ToggleGroup();
        RadioButton cardRadio = new RadioButton("Credit/Debit Card");
        cardRadio.setToggleGroup(paymentGroup);
        RadioButton paypalRadio = new RadioButton("Paypal");
        paypalRadio.setToggleGroup(paymentGroup);
        RadioButton cashRadio = new RadioButton("Cash");
        cashRadio.setToggleGroup(paymentGroup);
        cashRadio.setSelected(true);

        paymentMethodBox.getChildren().addAll(paymentMethodLabel, cardRadio, paypalRadio, cashRadio);
        centerBox.getChildren().add(paymentMethodBox);

        VBox cardInfoBox = new VBox(10);
        cardInfoBox.setPadding(new Insets(20));
        cardInfoBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 5;");
        cardInfoBox.setVisible(false);

        Label cardWarningLabel = new Label("DO NOT ENTER REAL CARD INFORMATION - This is a demo!");
        cardWarningLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #dc2626; -fx-wrap-text: true;");
        cardWarningLabel.setWrapText(true);
        
        Label cardNumberLabel = new Label("Card Number (Optional - Demo Only):");
        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("Leave empty or use test numbers only");
        Label expirationLabel = new Label("Expiration (Optional - Demo Only):");
        TextField expirationField = new TextField();
        expirationField.setPromptText("MM/YY");
        Label cvvLabel = new Label("CVV (Optional - Demo Only):");
        TextField cvvField = new TextField();
        cvvField.setPromptText("Leave empty");

        cardInfoBox.getChildren().addAll(cardWarningLabel, cardNumberLabel, cardNumberField, expirationLabel, expirationField, cvvLabel, cvvField);

        cardRadio.setOnAction(e -> cardInfoBox.setVisible(true));
        paypalRadio.setOnAction(e -> cardInfoBox.setVisible(false));
        cashRadio.setOnAction(e -> cardInfoBox.setVisible(false));

        centerBox.getChildren().add(cardInfoBox);

        Button checkoutButton = new Button("Checkout");
        checkoutButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 30;");
        checkoutButton.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                showAlert("Cart is empty!");
                return;
            }
            OrderDisplayWindow orderWindow = new OrderDisplayWindow(stage, currentOrder, null);
            orderWindow.show();
        });

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.getChildren().add(checkoutButton);
        centerBox.getChildren().add(buttonBox);

        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        VBox footerBox = CommonLayout.createFooter();
        root.setBottom(footerBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createItemRow(OrderItem item, int index) {
        HBox row = new HBox(15);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 5;");
        
        VBox itemInfo = new VBox(5);
        Label itemName = new Label(item.getName());
        itemName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        String details = "";
        if (item.getPizzaSize() != null) {
            details += item.getPizzaSize().getLabel();
        }
        if (item.getBeverageSize() != null) {
            details += item.getBeverageSize().getLabel();
        }
        if (item.getCrustType() != null) {
            details += " - " + item.getCrustType().getLabel();
        }
        
        Label itemDetails = new Label(details);
        itemDetails.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        itemInfo.getChildren().addAll(itemName, itemDetails);
        HBox.setHgrow(itemInfo, Priority.ALWAYS);

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 99, item.getQuantity());
        quantitySpinner.setPrefWidth(80);
        final int finalIndex = index;
        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (finalIndex < cartItems.size()) {
                OrderItem currentItem = cartItems.get(finalIndex);
                OrderItem updatedItem = new OrderItem(currentItem.getType(), currentItem.getName(), currentItem.getUnitPrice(), newVal);
                updatedItem.setPizzaSize(currentItem.getPizzaSize());
                updatedItem.setCrustType(currentItem.getCrustType());
                updatedItem.setToppings(currentItem.getToppings());
                updatedItem.setBeverageSize(currentItem.getBeverageSize());
                cartItems.set(finalIndex, updatedItem);
                updateCart();
                show();
            }
        });

        Label priceLabel = new Label("$" + String.format("%.2f", item.getTotalPrice()));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-min-width: 80;");

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 5 15;");
        removeButton.setOnAction(e -> {
            if (finalIndex < cartItems.size()) {
                cartItems.remove(finalIndex);
                updateCart();
                show();
            }
        });

        row.getChildren().addAll(itemInfo, quantitySpinner, priceLabel, removeButton);
        return row;
    }

    private void updateCart() {
        currentOrder = new Order("ORD-" + System.currentTimeMillis(), "guest");
        for (OrderItem item : cartItems) {
            currentOrder.addItem(item);
        }
        if (headerInfo != null) {
            headerInfo.updateCartItems(cartItems);
        }
        if (onCartUpdated != null) {
            onCartUpdated.run();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

