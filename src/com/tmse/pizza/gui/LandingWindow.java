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
 * Landing page - no login required
 * Shows Pizza/Specials/Drinks tabs matching hand-drawn design
 */
public class LandingWindow {
    private Stage stage;
    private Order currentOrder;
    private List<OrderItem> cartItems;
    private CommonLayout.HeaderInfo headerInfo;

    public LandingWindow(Stage primaryStage) {
        this(primaryStage, null, null);
    }
    
    public LandingWindow(Stage primaryStage, Order order, List<OrderItem> items) {
        this.stage = primaryStage;
        if (items != null) {
            this.cartItems = new ArrayList<>(items);
        } else {
            this.cartItems = new ArrayList<>();
        }
        if (order != null) {
            this.currentOrder = order;
        } else {
            this.currentOrder = new Order("ORD-" + System.currentTimeMillis(), "guest");
        }
    }

    public void show() {
        stage.setTitle("TMSE Pizza - Welcome");
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ffffff;");

        CommonLayout.HeaderInfo headerInfo = CommonLayout.createHeader(stage, currentOrder, cartItems, null, "menu");
        root.setTop(headerInfo.getHeaderContainer());
        
        // Store header info for cart button updates
        this.headerInfo = headerInfo;

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #ffffff;");
        
        Tab specialsTab = new Tab("Specials");
        specialsTab.setClosable(false);
        specialsTab.setContent(createSpecialsTab());
        
        Tab drinksTab = new Tab("Drinks");
        drinksTab.setClosable(false);
        drinksTab.setContent(createDrinksTab());
        
        Tab popularPizzasTab = new Tab("Popular Pizzas");
        popularPizzasTab.setClosable(false);
        popularPizzasTab.setContent(createPizzaTab());

        tabPane.getTabs().addAll(specialsTab, drinksTab, popularPizzasTab);
        root.setCenter(tabPane);
        
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

        Label noteLabel = new Label("All specials come in large");
        noteLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        Label sizeLabel = new Label("Select Pizza Size:");
        sizeLabel.setStyle("-fx-font-weight: bold;");
        
        HBox sizeBox = new HBox(10);
        ToggleGroup sizeGroup = new ToggleGroup();
        for (PizzaSize size : PizzaSize.values()) {
            RadioButton rb = new RadioButton(size.getLabel());
            rb.setToggleGroup(sizeGroup);
            rb.setUserData(size);
            if (size == PizzaSize.MEDIUM) rb.setSelected(true);
            sizeBox.getChildren().add(rb);
        }

        ScrollPane scrollPane = new ScrollPane();
        VBox pizzaListBox = new VBox(10);
        pizzaListBox.setPadding(new Insets(10));
        
        List<Pizza> regularPizzas = new ArrayList<>();
        for (Pizza pizza : MenuData.getPizzas()) {
            if (!pizza.getId().equals("build-your-own") && 
                !pizza.getId().equals("cowabunga-classic") &&
                !pizza.getId().equals("shredder-supreme") &&
                !pizza.getId().equals("mutant-veggie-melt") &&
                !pizza.getId().equals("ninja-chicken") &&
                !pizza.getId().equals("splinters-wisdom")) {
                regularPizzas.add(pizza);
            }
        }

        if (regularPizzas.isEmpty()) {
            Label noPizzasLabel = new Label("No regular pizzas available");
            noPizzasLabel.setStyle("-fx-text-fill: #6b7280;");
            pizzaListBox.getChildren().add(noPizzasLabel);
        } else {
            for (Pizza pizza : regularPizzas) {
                HBox pizzaBox = createPizzaCard(pizza, sizeGroup);
                pizzaListBox.getChildren().add(pizzaBox);
            }
        }

        scrollPane.setContent(pizzaListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);

        vbox.getChildren().addAll(noteLabel, sizeLabel, sizeBox, scrollPane);
        return vbox;
    }

    private VBox createSpecialsTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f9fafb;");

        Label noteLabel = new Label("All specials come in large");
        noteLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        Label sizeLabel = new Label("Select Pizza Size:");
        sizeLabel.setStyle("-fx-font-weight: bold;");
        
        HBox sizeBox = new HBox(10);
        ToggleGroup sizeGroup = new ToggleGroup();
        for (PizzaSize size : PizzaSize.values()) {
            RadioButton rb = new RadioButton(size.getLabel());
            rb.setToggleGroup(sizeGroup);
            rb.setUserData(size);
            if (size == PizzaSize.MEDIUM) rb.setSelected(true);
            sizeBox.getChildren().add(rb);
        }

        ScrollPane scrollPane = new ScrollPane();
        VBox pizzaListBox = new VBox(10);
        pizzaListBox.setPadding(new Insets(10));
        
        List<Pizza> specialtyPizzas = new ArrayList<>();
        for (Pizza pizza : MenuData.getPizzas()) {
            if (pizza.getId().equals("cowabunga-classic") ||
                pizza.getId().equals("shredder-supreme") ||
                pizza.getId().equals("mutant-veggie-melt") ||
                pizza.getId().equals("ninja-chicken") ||
                pizza.getId().equals("splinters-wisdom")) {
                specialtyPizzas.add(pizza);
            }
        }

        for (Pizza pizza : specialtyPizzas) {
            HBox pizzaBox = createPizzaCard(pizza, sizeGroup);
            pizzaListBox.getChildren().add(pizzaBox);
        }

        scrollPane.setContent(pizzaListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);

        vbox.getChildren().addAll(noteLabel, sizeLabel, sizeBox, scrollPane);
        return vbox;
    }

    private VBox createDrinksTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f9fafb;");

        Label noteLabel = new Label("All drinks offered are the same size irrespective of image.");
        noteLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

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

        ScrollPane scrollPane = new ScrollPane();
        VBox beverageListBox = new VBox(10);
        beverageListBox.setPadding(new Insets(10));

        for (Beverage beverage : MenuData.getBeverages()) {
            HBox beverageBox = createBeverageCard(beverage, sizeGroup);
            beverageListBox.getChildren().add(beverageBox);
        }

        scrollPane.setContent(beverageListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);

        vbox.getChildren().addAll(noteLabel, sizeLabel, sizeBox, scrollPane);
        return vbox;
    }

    private HBox createPizzaCard(Pizza pizza, ToggleGroup sizeGroup) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 2; -fx-border-radius: 8;");
        card.setPrefWidth(700);

        VBox leftSide = new VBox(10);
        leftSide.setAlignment(Pos.CENTER);
        
        StackPane pizzaPreview = createPizzaPreview(pizza.getDefaultToppings() != null ? pizza.getDefaultToppings() : new ArrayList<>());
        pizzaPreview.setPrefSize(120, 120);
        leftSide.getChildren().add(pizzaPreview);

        VBox details = new VBox(5);
        Label nameLabel = new Label(pizza.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label descLabel = new Label(pizza.getDescription());
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
        descLabel.setWrapText(true);

        RadioButton selectedSize = (RadioButton) sizeGroup.getSelectedToggle();
        PizzaSize size = selectedSize != null ? (PizzaSize) selectedSize.getUserData() : PizzaSize.MEDIUM;
        
        // Calculate toppings price (must be final for lambda)
        final double toppingsPrice;
        if (pizza.getDefaultToppings() != null) {
            double tempToppingsPrice = 0.0;
            for (String toppingId : pizza.getDefaultToppings()) {
                Topping topping = MenuData.getToppingById(toppingId);
                if (topping != null) {
                    tempToppingsPrice += topping.getPrice();
                }
            }
            toppingsPrice = tempToppingsPrice;
        } else {
            toppingsPrice = 0.0;
        }
        double price = pizza.getBasePrice() + size.getPrice() + toppingsPrice;
        
        HBox priceBox = new HBox(10);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        Label priceLabel = new Label("$" + String.format("%.2f", price));
        priceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
        
        // Update price when size changes
        sizeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                RadioButton newSize = (RadioButton) newVal;
                PizzaSize newSizeValue = (PizzaSize) newSize.getUserData();
                double newPrice = pizza.getBasePrice() + newSizeValue.getPrice() + toppingsPrice;
                priceLabel.setText("$" + String.format("%.2f", newPrice));
            }
        });
        
        Button addButton = new Button("Add to Cart →");
        addButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 8 16;");
        
        addButton.setOnAction(e -> {
            RadioButton currentSize = (RadioButton) sizeGroup.getSelectedToggle();
            PizzaSize currentSizeValue = currentSize != null ? (PizzaSize) currentSize.getUserData() : PizzaSize.MEDIUM;
            double currentToppingsPrice = 0.0;
            if (pizza.getDefaultToppings() != null) {
                for (String toppingId : pizza.getDefaultToppings()) {
                    Topping topping = MenuData.getToppingById(toppingId);
                    if (topping != null) {
                        currentToppingsPrice += topping.getPrice();
                    }
                }
            }
            double currentPrice = pizza.getBasePrice() + currentSizeValue.getPrice() + currentToppingsPrice;
            
            OrderItem item = new OrderItem("pizza", pizza.getName(), currentPrice, 1);
            item.setPizzaSize(currentSizeValue);
            item.setCrustType(CrustType.HAND_TOSSED);
            item.setToppings(pizza.getDefaultToppings() != null ? pizza.getDefaultToppings() : new ArrayList<>());
            
            cartItems.add(item);
            currentOrder.addItem(item);
            if (headerInfo != null) {
                headerInfo.updateCartItems(cartItems);
            }
            showAlert("Pizza added to cart!");
        });

        priceBox.getChildren().addAll(priceLabel, addButton);
        details.getChildren().addAll(nameLabel, descLabel, priceBox);
        card.getChildren().addAll(leftSide, details);

        return card;
    }
    
    private StackPane createPizzaPreview(List<String> toppings) {
        StackPane pane = new StackPane();
        pane.setPrefSize(120, 120);

        javafx.scene.shape.Circle pizzaBase = new javafx.scene.shape.Circle(50);
        pizzaBase.setFill(javafx.scene.paint.Color.rgb(220, 38, 38));
        pizzaBase.setStroke(javafx.scene.paint.Color.rgb(185, 28, 28));
        pizzaBase.setStrokeWidth(2);

        javafx.scene.shape.Circle cheese = new javafx.scene.shape.Circle(45);
        cheese.setFill(javafx.scene.paint.Color.rgb(254, 243, 199));
        cheese.setStroke(javafx.scene.paint.Color.rgb(251, 191, 36));
        cheese.setStrokeWidth(1);

        javafx.scene.shape.Circle crust = new javafx.scene.shape.Circle(55);
        crust.setFill(javafx.scene.paint.Color.rgb(217, 119, 6));
        crust.setStroke(javafx.scene.paint.Color.rgb(180, 83, 9));
        crust.setStrokeWidth(2);

        pane.getChildren().addAll(crust, pizzaBase, cheese);

        for (String toppingId : toppings) {
            Topping topping = MenuData.getToppingById(toppingId);
            if (topping != null) {
                java.util.Random random = new java.util.Random(toppingId.hashCode());
                int count = 6 + random.nextInt(10);
                List<Double> usedX = new ArrayList<>();
                List<Double> usedY = new ArrayList<>();
                
                for (int i = 0; i < count; i++) {
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double radius = 10 + random.nextDouble() * 35;
                    double x = Math.cos(angle) * radius;
                    double y = Math.sin(angle) * radius;
                    
                    boolean tooClose = false;
                    for (int j = 0; j < usedX.size(); j++) {
                        double dist = Math.sqrt(Math.pow(x - usedX.get(j), 2) + Math.pow(y - usedY.get(j), 2));
                        if (dist < 8) {
                            tooClose = true;
                            break;
                        }
                    }
                    
                    if (!tooClose) {
                        usedX.add(x);
                        usedY.add(y);
                        
                        javafx.scene.shape.Circle toppingCircle = new javafx.scene.shape.Circle(3 + random.nextDouble() * 2);
                        if (toppingId.equals("pepperoni")) {
                            toppingCircle.setFill(javafx.scene.paint.Color.rgb(153, 27, 27));
                        } else if (toppingId.equals("olives")) {
                            toppingCircle.setFill(javafx.scene.paint.Color.rgb(31, 41, 55));
                        } else if (toppingId.equals("mushrooms")) {
                            toppingCircle.setFill(javafx.scene.paint.Color.rgb(212, 196, 168));
                        } else if (toppingId.equals("sausage")) {
                            toppingCircle.setFill(javafx.scene.paint.Color.rgb(120, 53, 15));
                        } else if (toppingId.equals("bell-peppers")) {
                            toppingCircle.setFill(javafx.scene.paint.Color.rgb(34, 197, 94));
                        } else if (toppingId.equals("bacon")) {
                            toppingCircle.setFill(javafx.scene.paint.Color.rgb(220, 38, 38));
                        } else if (toppingId.equals("onions")) {
                            toppingCircle.setFill(javafx.scene.paint.Color.rgb(233, 213, 255));
                        } else {
                            toppingCircle.setFill(javafx.scene.paint.Color.rgb(100, 100, 100));
                        }
                        toppingCircle.setTranslateX(x);
                        toppingCircle.setTranslateY(y);
                        pane.getChildren().add(toppingCircle);
                    } else {
                        i--;
                    }
                }
            }
        }

        return pane;
    }

    private HBox createBeverageCard(Beverage beverage, ToggleGroup sizeGroup) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-width: 2; -fx-border-radius: 8;");
        card.setPrefWidth(700);

        Label emoji = new Label("");
        emoji.setStyle("-fx-font-size: 32px;");

        VBox details = new VBox(5);
        Label nameLabel = new Label(beverage.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label descLabel = new Label(beverage.getDescription());
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
        descLabel.setWrapText(true);

        RadioButton selectedSize = (RadioButton) sizeGroup.getSelectedToggle();
        BeverageSize size = selectedSize != null ? (BeverageSize) selectedSize.getUserData() : BeverageSize.MEDIUM;
        double price = beverage.getPrice(size);
        
        HBox priceBox = new HBox(10);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        Label priceLabel = new Label("$" + String.format("%.2f", price));
        priceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
        
        // Update price when size changes
        sizeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                RadioButton newSize = (RadioButton) newVal;
                BeverageSize newSizeValue = (BeverageSize) newSize.getUserData();
                double newPrice = beverage.getPrice(newSizeValue);
                priceLabel.setText("$" + String.format("%.2f", newPrice));
            }
        });
        
        Button addButton = new Button("Add to Cart →");
        addButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 8 16;");
        
        addButton.setOnAction(e -> {
            RadioButton currentSize = (RadioButton) sizeGroup.getSelectedToggle();
            BeverageSize currentSizeValue = currentSize != null ? (BeverageSize) currentSize.getUserData() : BeverageSize.MEDIUM;
            double currentPrice = beverage.getPrice(currentSizeValue);
            
            OrderItem item = new OrderItem("beverage", beverage.getName(), currentPrice, 1);
            item.setBeverageSize(currentSizeValue);
            
            cartItems.add(item);
            currentOrder.addItem(item);
            if (headerInfo != null) {
                headerInfo.updateCartItems(cartItems);
            }
            showAlert("Beverage added to cart!");
        });

        priceBox.getChildren().addAll(priceLabel, addButton);
        details.getChildren().addAll(nameLabel, descLabel, priceBox);
        card.getChildren().addAll(emoji, details);

        return card;
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

