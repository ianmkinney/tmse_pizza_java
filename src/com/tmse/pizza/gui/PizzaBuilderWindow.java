package com.tmse.pizza.gui;

import com.tmse.pizza.models.*;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pizza Builder Window
 * Pizza builder on the RIGHT side matching hand-drawn design
 */
public class PizzaBuilderWindow {
    private Stage stage;
    private Order currentOrder;
    private List<OrderItem> cartItems;
    private CommonLayout.HeaderInfo headerInfo;
    private PizzaSize selectedSize = PizzaSize.MEDIUM;
    private CrustType selectedCrust = CrustType.HAND_TOSSED;
    private String selectedCheese = "Mozzarella";
    private String selectedSauce = "Tomato Sauce";
    private List<String> selectedToppings = new ArrayList<>();
    private static final double BASE_PIZZA_PRICE = 7.99;
    private static final int MAX_TOPPINGS = 4;

    public PizzaBuilderWindow(Stage primaryStage, Order order, List<OrderItem> items) {
        this.stage = primaryStage;
        this.currentOrder = order;
        this.cartItems = items;
    }

    public void show() {
        stage.setTitle("TMSE Pizza - Custom Pizza Builder");
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
        }, "custom-pizza");
        root.setTop(headerInfo.getHeaderContainer());
        
        // Store header info for cart button updates
        this.headerInfo = headerInfo;

        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20));

        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        mainContent.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.NEVER);

        root.setCenter(mainContent);
        
        VBox footerBox = CommonLayout.createFooter();
        root.setBottom(footerBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createLeftPanel() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f9fafb;");

        Label titleLabel = new Label("Build your pizza!");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1f2937; " +
                "-fx-border-color: #dc2626; -fx-border-width: 4; -fx-background-color: #fef3c7; " +
                "-fx-padding: 15; -fx-background-radius: 5; -fx-border-radius: 5;");
        animatePulse(titleLabel);
        vbox.getChildren().add(titleLabel);

        Label sizeLabel = new Label("Size");
        sizeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        HBox sizeBox = new HBox(10);
        ToggleGroup sizeGroup = new ToggleGroup();
        for (PizzaSize size : PizzaSize.values()) {
            if (size == PizzaSize.PERSONAL) continue;
            RadioButton rb = new RadioButton(size.getLabel());
            rb.setToggleGroup(sizeGroup);
            rb.setUserData(size);
            if (size == PizzaSize.MEDIUM) {
                rb.setSelected(true);
                selectedSize = size;
            }
            rb.setOnAction(e -> selectedSize = (PizzaSize) rb.getUserData());
            sizeBox.getChildren().add(rb);
        }
        vbox.getChildren().addAll(sizeLabel, sizeBox);

        Label crustLabel = new Label("Crust");
        crustLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
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
        crustCombo.setOnAction(e -> {
            selectedCrust = crustCombo.getValue();
            updatePizzaPreview();
        });
        vbox.getChildren().addAll(crustLabel, crustCombo);

        Label cheeseLabel = new Label("Cheese");
        cheeseLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        ComboBox<String> cheeseCombo = new ComboBox<>();
        cheeseCombo.getItems().addAll("Mozzarella", "Extra Cheese");
        cheeseCombo.setValue("Mozzarella");
        cheeseCombo.setOnAction(e -> {
            selectedCheese = cheeseCombo.getValue();
            updatePizzaPreview();
        });
        vbox.getChildren().addAll(cheeseLabel, cheeseCombo);

        Label sauceLabel = new Label("Sauce");
        sauceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        ComboBox<String> sauceCombo = new ComboBox<>();
        sauceCombo.getItems().addAll("Tomato Sauce", "Alfredo Sauce", "BBQ Sauce");
        sauceCombo.setValue("Tomato Sauce");
        sauceCombo.setOnAction(e -> {
            selectedSauce = sauceCombo.getValue();
            updatePizzaPreview();
        });
        vbox.getChildren().addAll(sauceLabel, sauceCombo);

        Label toppingsLabel = new Label("Toppings (multiple) - " + selectedToppings.size() + "/" + MAX_TOPPINGS + " selected");
        toppingsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        GridPane toppingsGrid = new GridPane();
        toppingsGrid.setHgap(10);
        toppingsGrid.setVgap(10);
        toppingsGrid.setPadding(new Insets(10));
        toppingsGrid.setStyle("-fx-border-color: #d1d5db; -fx-border-width: 2; -fx-background-color: #ffffff; -fx-border-radius: 5;");

        List<CheckBox> toppingCheckboxes = new ArrayList<>();
        int col = 0;
        int row = 0;
        for (Topping topping : MenuData.getToppings()) {
            CheckBox cb = new CheckBox(topping.getName() + " (+$" + String.format("%.2f", topping.getPrice()) + ")");
            cb.setUserData(topping);
            cb.setOnAction(e -> {
                if (cb.isSelected()) {
                    if (selectedToppings.size() >= MAX_TOPPINGS) {
                        cb.setSelected(false);
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Maximum Toppings");
                        alert.setHeaderText(null);
                        alert.setContentText("Maximum " + MAX_TOPPINGS + " toppings allowed! Please deselect a topping first.");
                        alert.showAndWait();
                        return;
                    }
                    selectedToppings.add(topping.getId());
                } else {
                    selectedToppings.remove(topping.getId());
                }
                toppingsLabel.setText("Toppings (multiple) - " + selectedToppings.size() + "/" + MAX_TOPPINGS + " selected");
                updatePizzaPreview();
            });
            toppingCheckboxes.add(cb);
            toppingsGrid.add(cb, col, row);
            col++;
            if (col > 1) {
                col = 0;
                row++;
            }
        }
        vbox.getChildren().addAll(toppingsLabel, toppingsGrid);

        Label instructionsLabel = new Label("Special Instructions:");
        instructionsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        TextArea instructionsField = new TextArea();
        instructionsField.setPromptText("Free text");
        instructionsField.setPrefRowCount(4);
        instructionsField.setWrapText(true);
        vbox.getChildren().addAll(instructionsLabel, instructionsField);

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 24;");
        
        double totalPrice = calculatePrice();
        Label priceLabel = new Label("$" + String.format("%.2f", totalPrice));
        priceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");

        addToCartButton.setOnAction(e -> {
            double price = calculatePrice();
            OrderItem item = new OrderItem("pizza", "Build Your Own", price, 1);
            item.setPizzaSize(selectedSize);
            item.setCrustType(selectedCrust);
            item.setToppings(new ArrayList<>(selectedToppings));
            if (!instructionsField.getText().trim().isEmpty()) {
                item.setSpecialInstructions(instructionsField.getText().trim());
            }
            
            cartItems.add(item);
            currentOrder.addItem(item);
            if (headerInfo != null) {
                headerInfo.updateCartItems(cartItems);
            }
            
            showAlert("Custom pizza added to cart!");
            
            selectedToppings.clear();
            for (CheckBox cb : toppingCheckboxes) {
                cb.setSelected(false);
            }
            instructionsField.clear();
            toppingsLabel.setText("Toppings (multiple) - " + selectedToppings.size() + "/" + MAX_TOPPINGS + " selected");
            updatePizzaPreview();
        });

        HBox buttonBox = new HBox(20);
        buttonBox.getChildren().addAll(addToCartButton, priceLabel);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        vbox.getChildren().add(buttonBox);

        return vbox;
    }

    private VBox createRightPanel() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #fef3c7; -fx-border-color: #dc2626; -fx-border-width: 4; -fx-border-radius: 5;");
        vbox.setPrefWidth(400);
        vbox.setAlignment(Pos.CENTER);

        Label previewLabel = new Label("Select details below, see your pizza preview!");
        previewLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1f2937; " +
                "-fx-border-color: #dc2626; -fx-border-width: 4; -fx-background-color: #fef3c7; " +
                "-fx-padding: 15; -fx-background-radius: 5; -fx-border-radius: 5; -fx-text-alignment: center;");
        previewLabel.setWrapText(true);
        previewLabel.setAlignment(Pos.CENTER);
        animatePopShake(previewLabel);
        vbox.getChildren().add(previewLabel);

        StackPane pizzaPane = createPizzaPreview();
        vbox.getChildren().add(pizzaPane);
        
        VBox.setVgrow(pizzaPane, Priority.ALWAYS);

        return vbox;
    }

    private StackPane pizzaPreviewPane;
    
    private StackPane createPizzaPreview() {
        pizzaPreviewPane = new StackPane();
        pizzaPreviewPane.setPrefSize(300, 300);
        updatePizzaPreview();
        return pizzaPreviewPane;
    }
    
    private void updatePizzaPreview() {
        if (pizzaPreviewPane == null) return;
        
        pizzaPreviewPane.getChildren().clear();

        // Set sauce color based on selection
        Color sauceColor;
        Color sauceStrokeColor;
        if ("Alfredo Sauce".equals(selectedSauce)) {
            sauceColor = Color.rgb(255, 250, 240); // Cream/white for alfredo
            sauceStrokeColor = Color.rgb(245, 240, 230);
        } else if ("BBQ Sauce".equals(selectedSauce)) {
            sauceColor = Color.rgb(139, 69, 19); // Dark brown for BBQ
            sauceStrokeColor = Color.rgb(101, 50, 14);
        } else { // Tomato Sauce (default)
            sauceColor = Color.rgb(220, 38, 38); // Red for tomato
            sauceStrokeColor = Color.rgb(185, 28, 28);
        }

        Circle pizzaBase = new Circle(120);
        pizzaBase.setFill(sauceColor);
        pizzaBase.setStroke(sauceStrokeColor);
        pizzaBase.setStrokeWidth(3);

        // Set cheese color based on selection
        Color cheeseColor;
        Color cheeseStrokeColor;
        if ("Extra Cheese".equals(selectedCheese)) {
            cheeseColor = Color.rgb(255, 235, 150); // Richer yellow for extra cheese
            cheeseStrokeColor = Color.rgb(255, 200, 50);
        } else { // Mozzarella (default)
            cheeseColor = Color.rgb(254, 243, 199); // Light cream for mozzarella
            cheeseStrokeColor = Color.rgb(251, 191, 36);
        }

        Circle cheese = new Circle(110);
        cheese.setFill(cheeseColor);
        cheese.setStroke(cheeseStrokeColor);
        cheese.setStrokeWidth(2);

        // Set crust appearance based on selected crust type
        double crustRadius;
        Color crustFillColor;
        Color crustStrokeColor;
        double crustStrokeWidth;
        
        switch (selectedCrust) {
            case THIN_NINJA:
                crustRadius = 125; // Thinner crust
                crustFillColor = Color.rgb(230, 140, 20); // Lighter brown
                crustStrokeColor = Color.rgb(200, 100, 10);
                crustStrokeWidth = 2;
                break;
            case DEEP_DISH:
                crustRadius = 140; // Thicker crust
                crustFillColor = Color.rgb(200, 100, 10); // Darker brown
                crustStrokeColor = Color.rgb(150, 70, 5);
                crustStrokeWidth = 4;
                break;
            case OOZE_FILLED:
                crustRadius = 135; // Slightly thicker for filled crust
                crustFillColor = Color.rgb(240, 180, 60); // Golden/yellowish for cheese-filled
                crustStrokeColor = Color.rgb(217, 119, 6);
                crustStrokeWidth = 3;
                break;
            case HAND_TOSSED:
            default:
                crustRadius = 130; // Regular thickness
                crustFillColor = Color.rgb(217, 119, 6);
                crustStrokeColor = Color.rgb(180, 83, 9);
                crustStrokeWidth = 3;
                break;
        }

        Circle crust = new Circle(crustRadius);
        crust.setFill(crustFillColor);
        crust.setStroke(crustStrokeColor);
        crust.setStrokeWidth(crustStrokeWidth);

        pizzaPreviewPane.getChildren().addAll(crust, pizzaBase, cheese);

        for (String toppingId : selectedToppings) {
            Topping topping = MenuData.getToppingById(toppingId);
            if (topping != null) {
                Random random = new Random(toppingId.hashCode() + System.currentTimeMillis());
                int count = 6 + random.nextInt(10);
                List<Double> usedX = new ArrayList<>();
                List<Double> usedY = new ArrayList<>();
                
                for (int i = 0; i < count; i++) {
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double radius = 15 + random.nextDouble() * 75;
                    double x = Math.cos(angle) * radius;
                    double y = Math.sin(angle) * radius;
                    
                    boolean tooClose = false;
                    for (int j = 0; j < usedX.size(); j++) {
                        double dist = Math.sqrt(Math.pow(x - usedX.get(j), 2) + Math.pow(y - usedY.get(j), 2));
                        if (dist < 15) {
                            tooClose = true;
                            break;
                        }
                    }
                    
                    if (!tooClose) {
                        usedX.add(x);
                        usedY.add(y);
                        
                        Circle toppingCircle = new Circle(6 + random.nextDouble() * 4);
                        if (toppingId.equals("pepperoni")) {
                            toppingCircle.setFill(Color.rgb(153, 27, 27));
                        } else if (toppingId.equals("olives")) {
                            toppingCircle.setFill(Color.rgb(31, 41, 55));
                        } else if (toppingId.equals("mushrooms")) {
                            toppingCircle.setFill(Color.rgb(212, 196, 168));
                        } else if (toppingId.equals("sausage")) {
                            toppingCircle.setFill(Color.rgb(120, 53, 15));
                        } else if (toppingId.equals("bell-peppers")) {
                            toppingCircle.setFill(Color.rgb(34, 197, 94));
                        } else if (toppingId.equals("bacon")) {
                            toppingCircle.setFill(Color.rgb(220, 38, 38));
                        } else if (toppingId.equals("onions")) {
                            toppingCircle.setFill(Color.rgb(233, 213, 255));
                        } else {
                            toppingCircle.setFill(Color.rgb(100, 100, 100));
                        }
                        toppingCircle.setTranslateX(x);
                        toppingCircle.setTranslateY(y);
                        
                        FadeTransition fade = new FadeTransition(Duration.millis(500), toppingCircle);
                        fade.setFromValue(0);
                        fade.setToValue(1);
                        fade.setDelay(Duration.millis(i * 50));
                        fade.play();
                        
                        pizzaPreviewPane.getChildren().add(toppingCircle);
                    } else {
                        i--;
                    }
                }
            }
        }

    }

    private double calculatePrice() {
        double price = BASE_PIZZA_PRICE;
        price += selectedSize.getPrice();
        for (String toppingId : selectedToppings) {
            Topping topping = MenuData.getToppingById(toppingId);
            if (topping != null) {
                price += topping.getPrice();
            }
        }
        return price;
    }

    private void animatePulse(Label label) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), label);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.15);
        scaleTransition.setToY(1.15);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(Animation.INDEFINITE);
        scaleTransition.play();
    }

    private void animatePopShake(Label label) {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        
        KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0), e -> {
            label.setScaleX(1.0);
            label.setScaleY(1.0);
            label.setTranslateX(0);
            label.setTranslateY(0);
        });
        
        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(4.8), e -> {
            label.setScaleX(1.0);
            label.setScaleY(1.0);
            label.setTranslateX(0);
            label.setTranslateY(0);
        });
        
        KeyFrame keyFrame3 = new KeyFrame(Duration.seconds(4.9), e -> {
            label.setScaleX(1.2);
            label.setScaleY(1.2);
            label.setTranslateY(-10);
        });
        
        KeyFrame keyFrame4 = new KeyFrame(Duration.seconds(4.95), e -> {
            label.setTranslateX(-5);
        });
        
        KeyFrame keyFrame5 = new KeyFrame(Duration.seconds(5.0), e -> {
            label.setTranslateX(5);
        });
        
        KeyFrame keyFrame6 = new KeyFrame(Duration.seconds(5.05), e -> {
            label.setTranslateX(-5);
        });
        
        KeyFrame keyFrame7 = new KeyFrame(Duration.seconds(5.1), e -> {
            label.setScaleX(1.0);
            label.setScaleY(1.0);
            label.setTranslateX(0);
            label.setTranslateY(0);
        });
        
        timeline.getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3, keyFrame4, keyFrame5, keyFrame6, keyFrame7);
        timeline.play();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

