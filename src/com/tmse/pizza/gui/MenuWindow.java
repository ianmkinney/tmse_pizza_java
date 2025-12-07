package com.tmse.pizza.gui;

import com.tmse.pizza.models.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu window for ordering pizzas and beverages
 */
public class MenuWindow {
    private Stage stage;
    private User currentUser;
    private Order currentOrder;
    private List<OrderItem> cartItems;

    public MenuWindow(Stage primaryStage, User user) {
        this.stage = primaryStage;
        this.currentUser = user;
        this.cartItems = new ArrayList<>();
        this.currentOrder = new Order("ORD-" + System.currentTimeMillis(), user.getUsername());
    }

    public void show() {
        stage.setTitle("TMSE Pizza - Menu");
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ffffff;");
        
        Order tempOrder = new Order("ORD-" + System.currentTimeMillis(), currentUser.getUsername());
        List<OrderItem> tempCart = new ArrayList<>();
        for (OrderItem item : cartItems) {
            tempCart.add(item);
        }
        
        CommonLayout.HeaderInfo headerInfo = CommonLayout.createHeader(stage, tempOrder, tempCart, () -> {
            LandingWindow landingWindow = new LandingWindow(stage);
            landingWindow.show();
        }, "menu");
        root.setTop(headerInfo.getHeaderContainer());
        
        // Store header info for cart button updates
        this.headerInfo = headerInfo;

        TabPane tabPane = new TabPane();
        
        Tab pizzaTab = new Tab("Pizza");
        pizzaTab.setClosable(false);
        pizzaTab.setContent(createPizzaTab());
        
        Tab beverageTab = new Tab("Beverages");
        beverageTab.setClosable(false);
        beverageTab.setContent(createBeverageTab());

        tabPane.getTabs().addAll(pizzaTab, beverageTab);
        root.setCenter(tabPane);

        VBox cartBox = createCartPanel();
        root.setRight(cartBox);
        
        VBox footerBox = CommonLayout.createFooter();
        root.setBottom(footerBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Full screen is already set at startup, no need to toggle
    }

    private VBox createPizzaTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f9fafb;");

        Label sizeLabel = new Label("Select Pizza Size:");
        sizeLabel.setStyle("-fx-font-weight: bold;");
        
        HBox sizeBox = new HBox(10);
        ToggleGroup sizeGroup = new ToggleGroup();
        for (PizzaSize size : PizzaSize.values()) {
            RadioButton rb = new RadioButton(size.getLabel() + " (+$" + String.format("%.2f", size.getPrice()) + ")");
            rb.setToggleGroup(sizeGroup);
            rb.setUserData(size);
            if (size == PizzaSize.MEDIUM) rb.setSelected(true);
            sizeBox.getChildren().add(rb);
        }

        Label crustLabel = new Label("Select Crust:");
        crustLabel.setStyle("-fx-font-weight: bold;");
        
        ComboBox<CrustType> crustCombo = new ComboBox<>();
        crustCombo.getItems().addAll(CrustType.values());
        crustCombo.setValue(CrustType.HAND_TOSSED);
        crustCombo.setConverter(new javafx.util.StringConverter<CrustType>() {
            @Override
            public String toString(CrustType object) {
                return object.getLabel();
            }
            @Override
            public CrustType fromString(String string) {
                return null;
            }
        });

        Label pizzaLabel = new Label("Select Pizza:");
        pizzaLabel.setStyle("-fx-font-weight: bold;");
        
        ListView<Pizza> pizzaList = new ListView<>();
        pizzaList.getItems().addAll(MenuData.getPizzas());
        pizzaList.setCellFactory(param -> new ListCell<Pizza>() {
            @Override
            protected void updateItem(Pizza pizza, boolean empty) {
                super.updateItem(pizza, empty);
                if (empty || pizza == null) {
                    setText(null);
                } else {
                    setText(pizza.getName() + " - $" + String.format("%.2f", pizza.getBasePrice()));
                }
            }
        });

        Label toppingsLabel = new Label("Select Toppings (up to 4):");
        toppingsLabel.setStyle("-fx-font-weight: bold;");
        
        VBox toppingsBox = new VBox(5);
        List<CheckBox> toppingCheckboxes = new ArrayList<>();
        for (Topping topping : MenuData.getToppings()) {
            CheckBox cb = new CheckBox(topping.getName() + " (+$" + String.format("%.2f", topping.getPrice()) + ")");
            cb.setUserData(topping);
            toppingCheckboxes.add(cb);
            toppingsBox.getChildren().add(cb);
        }

        Button addPizzaButton = new Button("Add Pizza to Cart");
        addPizzaButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 10 20;");
        
        addPizzaButton.setOnAction(e -> {
            Pizza selectedPizza = pizzaList.getSelectionModel().getSelectedItem();
            if (selectedPizza == null) {
                showAlert("Please select a pizza");
                return;
            }

            RadioButton selectedSize = (RadioButton) sizeGroup.getSelectedToggle();
            PizzaSize size = (PizzaSize) selectedSize.getUserData();
            CrustType crust = crustCombo.getValue();

            List<String> selectedToppings = new ArrayList<>();
            for (CheckBox cb : toppingCheckboxes) {
                if (cb.isSelected()) {
                    Topping topping = (Topping) cb.getUserData();
                    selectedToppings.add(topping.getId());
                }
            }

            if (selectedToppings.size() > 4) {
                showAlert("Maximum 4 toppings allowed");
                return;
            }

            double toppingsPrice = selectedToppings.stream()
                .mapToDouble(id -> MenuData.getToppingById(id).getPrice())
                .sum();
            
            double unitPrice = selectedPizza.getBasePrice() + size.getPrice() + toppingsPrice;
            
            OrderItem item = new OrderItem("pizza", selectedPizza.getName(), unitPrice, 1);
            item.setPizzaSize(size);
            item.setCrustType(crust);
            item.setToppings(selectedToppings);
            
            cartItems.add(item);
            currentOrder.addItem(item);
            updateCartDisplay();
            if (headerInfo != null) {
                headerInfo.updateCartItems(cartItems);
            }
            
            showAlert("Pizza added to cart!");
        });

        ScrollPane scrollPane = new ScrollPane(toppingsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);

        vbox.getChildren().addAll(sizeLabel, sizeBox, crustLabel, crustCombo,
                pizzaLabel, pizzaList, toppingsLabel, scrollPane, addPizzaButton);

        return vbox;
    }

    private VBox createBeverageTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f9fafb;");

        Label sizeLabel = new Label("Select Beverage Size:");
        sizeLabel.setStyle("-fx-font-weight: bold;");
        
        HBox sizeBox = new HBox(10);
        ToggleGroup sizeGroup = new ToggleGroup();
        for (BeverageSize size : BeverageSize.values()) {
            RadioButton rb = new RadioButton(size.getLabel());
            rb.setToggleGroup(sizeGroup);
            rb.setUserData(size);
            if (size == BeverageSize.MEDIUM) rb.setSelected(true);
            sizeBox.getChildren().add(rb);
        }

        Label beverageLabel = new Label("Select Beverage:");
        beverageLabel.setStyle("-fx-font-weight: bold;");
        
        ListView<Beverage> beverageList = new ListView<>();
        beverageList.getItems().addAll(MenuData.getBeverages());
        beverageList.setCellFactory(param -> new ListCell<Beverage>() {
            @Override
            protected void updateItem(Beverage beverage, boolean empty) {
                super.updateItem(beverage, empty);
                if (empty || beverage == null) {
                    setText(null);
                } else {
                    setText(beverage.getName() + " - " + beverage.getDescription());
                }
            }
        });

        Button addBeverageButton = new Button("Add Beverage to Cart");
        addBeverageButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 10 20;");
        
        addBeverageButton.setOnAction(e -> {
            Beverage selectedBeverage = beverageList.getSelectionModel().getSelectedItem();
            if (selectedBeverage == null) {
                showAlert("Please select a beverage");
                return;
            }

            RadioButton selectedSize = (RadioButton) sizeGroup.getSelectedToggle();
            BeverageSize size = (BeverageSize) selectedSize.getUserData();
            double price = selectedBeverage.getPrice(size);

            OrderItem item = new OrderItem("beverage", selectedBeverage.getName(), price, 1);
            item.setBeverageSize(size);
            
            cartItems.add(item);
            currentOrder.addItem(item);
            updateCartDisplay();
            if (headerInfo != null) {
                headerInfo.updateCartItems(cartItems);
            }
            
            showAlert("Beverage added to cart!");
        });

        vbox.getChildren().addAll(sizeLabel, sizeBox, beverageLabel, beverageList, addBeverageButton);

        return vbox;
    }

    private VBox createCartPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 1;");
        vbox.setPrefWidth(300);

        Label cartLabel = new Label("Cart");
        cartLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        cartListView = new ListView<>();
        cartListView.setItems(javafx.collections.FXCollections.observableArrayList(cartItems));
        cartListView.setCellFactory(param -> new ListCell<OrderItem>() {
            @Override
            protected void updateItem(OrderItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String details = item.getName();
                    if (item.getPizzaSize() != null) {
                        details += " - " + item.getPizzaSize().getLabel();
                    }
                    if (item.getBeverageSize() != null) {
                        details += " - " + item.getBeverageSize().getLabel();
                    }
                    details += " - $" + String.format("%.2f", item.getTotalPrice());
                    setText(details);
                }
            }
        });

        subtotalLabel = new Label("Subtotal: $0.00");
        taxLabel = new Label("Tax (8%): $0.00");
        totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button viewOrderButton = new Button("View Order");
        viewOrderButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 10 20;");
        viewOrderButton.setOnAction(e -> {
            if (cartItems.isEmpty()) {
                showAlert("Cart is empty");
                return;
            }
            OrderDisplayWindow orderWindow = new OrderDisplayWindow(stage, currentOrder, currentUser);
            orderWindow.show();
        });

        Button clearCartButton = new Button("Clear Cart");
        clearCartButton.setOnAction(e -> {
            cartItems.clear();
            currentOrder = new Order("ORD-" + System.currentTimeMillis(), currentUser.getUsername());
            updateCartDisplay();
            if (headerInfo != null) {
                headerInfo.updateCartItems(cartItems);
            }
        });

        vbox.getChildren().addAll(cartLabel, cartListView, subtotalLabel, taxLabel, totalLabel,
                viewOrderButton, clearCartButton);

        updateCartDisplay();

        return vbox;
    }

    private ListView<OrderItem> cartListView;
    private Label subtotalLabel;
    private Label taxLabel;
    private Label totalLabel;
    private CommonLayout.HeaderInfo headerInfo;

    private void updateCartDisplay() {
        if (cartListView != null) {
            cartListView.setItems(javafx.collections.FXCollections.observableArrayList(cartItems));
        }
        if (subtotalLabel != null) {
            subtotalLabel.setText("Subtotal: $" + String.format("%.2f", currentOrder.getSubtotal()));
        }
        if (taxLabel != null) {
            taxLabel.setText("Tax (8%): $" + String.format("%.2f", currentOrder.getTax()));
        }
        if (totalLabel != null) {
            totalLabel.setText("Total: $" + String.format("%.2f", currentOrder.getTotal()));
        }
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

