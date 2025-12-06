package com.tmse.pizza.gui;

import com.tmse.pizza.models.*;
import com.tmse.pizza.storage.FileStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Driver Dashboard Window
 * Allows drivers to claim orders, view deliveries, and track tips
 */
public class DriverWindow {
    private Stage stage;
    private User currentUser;
    private ObservableList<Order> availableOrdersList;
    private ObservableList<Order> myDeliveriesList;
    private ObservableList<FileStorage.TipRecord> tipsList;
    private TableView<Order> availableOrdersTable;
    private TableView<Order> myDeliveriesTable;
    private TableView<FileStorage.TipRecord> tipsTable;

    public DriverWindow(Stage primaryStage, User user) {
        this.stage = primaryStage;
        this.currentUser = user;
        this.availableOrdersList = FXCollections.observableArrayList();
        this.myDeliveriesList = FXCollections.observableArrayList();
        this.tipsList = FXCollections.observableArrayList();
    }

    public void show() {
        stage.setTitle("TMSE Pizza - Driver Dashboard");
        double currentWidth = stage.getWidth() > 0 ? stage.getWidth() : 1400;
        double currentHeight = stage.getHeight() > 0 ? stage.getHeight() : 900;
        stage.setWidth(currentWidth);
        stage.setHeight(currentHeight);
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f9fafb;");

        // Header
        HBox headerBox = new HBox(20);
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #dc2626, #f59e0b); -fx-padding: 15 20;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label headerLabel = new Label("Driver Dashboard");
        headerLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-padding: 8 15;");
        logoutButton.setOnAction(e -> {
            LoginWindow loginWindow = new LoginWindow(stage);
            loginWindow.show();
        });

        headerBox.getChildren().addAll(headerLabel, spacer, logoutButton);
        root.setTop(headerBox);

        // Main content with tabs
        TabPane tabPane = new TabPane();

        // Available Orders Tab
        Tab availableTab = new Tab("Available Orders");
        availableTab.setClosable(false);
        availableTab.setContent(createAvailableOrdersTab());
        
        // My Deliveries Tab
        Tab deliveriesTab = new Tab("My Deliveries");
        deliveriesTab.setClosable(false);
        deliveriesTab.setContent(createMyDeliveriesTab());
        
        // Tips Tab
        Tab tipsTab = new Tab("Tip Tracker");
        tipsTab.setClosable(false);
        tipsTab.setContent(createTipsTab());

        tabPane.getTabs().addAll(availableTab, deliveriesTab, tipsTab);
        root.setCenter(tabPane);

        // Footer
        VBox footerBox = CommonLayout.createFooter();
        root.setBottom(footerBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        // Load data
        refreshAvailableOrders();
        refreshMyDeliveries();
        refreshTips();
    }

    private VBox createAvailableOrdersTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label titleLabel = new Label("Available Delivery Orders");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label infoLabel = new Label("Click 'Claim Order' to assign yourself to a delivery");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        availableOrdersTable = new TableView<>();
        availableOrdersTable.setItems(availableOrdersList);
        availableOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Order, String> orderIdCol = new TableColumn<>("Order #");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                order.getCustomerName() != null ? order.getCustomerName() : order.getUsername()
            );
        });

        TableColumn<Order, String> addressCol = new TableColumn<>("Delivery Address");
        addressCol.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "N/A"
            );
        });

        TableColumn<Order, String> itemsCol = new TableColumn<>("Items");
        itemsCol.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            String items = order.getItems().stream()
                .map(item -> item.getQuantity() + "x " + item.getName())
                .collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(items);
        });

        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("$" + String.format("%.2f", cellData.getValue().getTotal())));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Order, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<Order, Void>() {
            private final Button claimButton = new Button("Claim Order");

            {
                claimButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 5 10;");
                claimButton.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    claimOrder(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(claimButton);
                }
            }
        });

        availableOrdersTable.getColumns().addAll(orderIdCol, customerCol, addressCol, itemsCol, totalCol, statusCol, actionsCol);

        ScrollPane scrollPane = new ScrollPane(availableOrdersTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 8 15;");
        refreshButton.setOnAction(e -> refreshAvailableOrders());

        vbox.getChildren().addAll(titleLabel, infoLabel, refreshButton, scrollPane);
        return vbox;
    }

    private VBox createMyDeliveriesTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        // Stats
        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER_LEFT);

        Label activeLabel = createStatCard("Active", "0", "#3b82f6");
        Label completedLabel = createStatCard("Completed", "0", "#10b981");

        statsBox.getChildren().addAll(activeLabel, completedLabel);

        Label titleLabel = new Label("My Deliveries");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        myDeliveriesTable = new TableView<>();
        myDeliveriesTable.setItems(myDeliveriesList);
        myDeliveriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Order, String> orderIdCol = new TableColumn<>("Order #");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                order.getCustomerName() != null ? order.getCustomerName() : order.getUsername()
            );
        });

        TableColumn<Order, String> addressCol = new TableColumn<>("Delivery Address");
        addressCol.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "N/A"
            );
        });

        TableColumn<Order, String> itemsCol = new TableColumn<>("Items");
        itemsCol.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            String items = order.getItems().stream()
                .map(item -> item.getQuantity() + "x " + item.getName())
                .collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(items);
        });

        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("$" + String.format("%.2f", cellData.getValue().getTotal())));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Order, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<Order, Void>() {
            private final Button completeButton = new Button("Complete Delivery");
            private final HBox tipBox = new HBox(5);
            private final TextField tipField = new TextField();
            private final Button saveTipButton = new Button("Save Tip");

            {
                completeButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 5 10;");
                completeButton.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    completeDelivery(order);
                });

                tipField.setPromptText("Tip amount");
                tipField.setPrefWidth(100);
                saveTipButton.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 5 10;");
                saveTipButton.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    String tipText = tipField.getText().trim();
                    if (!tipText.isEmpty()) {
                        try {
                            double tipAmount = Double.parseDouble(tipText);
                            saveTip(order.getOrderId(), tipAmount);
                            tipField.clear();
                        } catch (NumberFormatException ex) {
                            showAlert("Please enter a valid number");
                        }
                    }
                });
                tipBox.getChildren().addAll(tipField, saveTipButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(5);
                    
                    if ("out-for-delivery".equals(order.getStatus())) {
                        box.getChildren().add(completeButton);
                    }
                    
                    if ("delivered".equals(order.getStatus())) {
                        box.getChildren().add(tipBox);
                    }
                    
                    setGraphic(box);
                }
            }
        });

        myDeliveriesTable.getColumns().addAll(orderIdCol, customerCol, addressCol, itemsCol, totalCol, statusCol, actionsCol);

        ScrollPane scrollPane = new ScrollPane(myDeliveriesTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 8 15;");
        refreshButton.setOnAction(e -> {
            refreshMyDeliveries();
            refreshStats();
        });

        vbox.getChildren().addAll(statsBox, titleLabel, refreshButton, scrollPane);
        return vbox;
    }

    private VBox createTipsTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label titleLabel = new Label("Tip Tracker");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Total tips summary
        Label totalTipsLabel = new Label("Total Tips: $0.00");
        totalTipsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #10b981;");
        this.totalTipsLabel = totalTipsLabel;

        tipsTable = new TableView<>();
        tipsTable.setItems(tipsList);
        tipsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<FileStorage.TipRecord, String> orderIdCol = new TableColumn<>("Order #");
        orderIdCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getOrderId()));

        TableColumn<FileStorage.TipRecord, String> amountCol = new TableColumn<>("Tip Amount");
        amountCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("$" + String.format("%.2f", cellData.getValue().getAmount())));

        TableColumn<FileStorage.TipRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));

        tipsTable.getColumns().addAll(orderIdCol, amountCol, dateCol);

        ScrollPane scrollPane = new ScrollPane(tipsTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 8 15;");
        refreshButton.setOnAction(e -> {
            refreshTips();
            updateTotalTips();
        });

        vbox.getChildren().addAll(titleLabel, totalTipsLabel, refreshButton, scrollPane);
        return vbox;
    }

    private Label totalTipsLabel;

    private void claimOrder(Order order) {
        order.setAssignedDriverId(currentUser.getUsername());
        order.setAssignedDriverName(currentUser.getUsername());
        order.setStatus("out-for-delivery");
        
        try {
            FileStorage.updateOrder(order);
            showAlert("Order claimed successfully!");
            refreshAvailableOrders();
            refreshMyDeliveries();
        } catch (IOException ex) {
            showAlert("Error claiming order: " + ex.getMessage());
        }
    }

    private void completeDelivery(Order order) {
        order.setStatus("delivered");
        
        try {
            FileStorage.updateOrder(order);
            showAlert("Delivery completed!");
            refreshMyDeliveries();
            refreshAvailableOrders();
        } catch (IOException ex) {
            showAlert("Error completing delivery: " + ex.getMessage());
        }
    }

    private void saveTip(String orderId, double tipAmount) {
        try {
            FileStorage.saveTip(orderId, currentUser.getUsername(), tipAmount);
            showAlert("Tip saved!");
            refreshTips();
            updateTotalTips();
        } catch (IOException ex) {
            showAlert("Error saving tip: " + ex.getMessage());
        }
    }

    private void refreshAvailableOrders() {
        try {
            List<Order> available = FileStorage.getAvailableDeliveryOrders();
            availableOrdersList.setAll(available);
        } catch (IOException ex) {
            showAlert("Error loading available orders: " + ex.getMessage());
        }
    }

    private void refreshMyDeliveries() {
        try {
            List<Order> myOrders = FileStorage.getOrdersByDriver(currentUser.getUsername());
            myDeliveriesList.setAll(myOrders);
        } catch (IOException ex) {
            showAlert("Error loading deliveries: " + ex.getMessage());
        }
    }

    private void refreshTips() {
        try {
            List<FileStorage.TipRecord> tips = FileStorage.getTipsByDriver(currentUser.getUsername());
            tipsList.setAll(tips);
        } catch (IOException ex) {
            showAlert("Error loading tips: " + ex.getMessage());
        }
    }

    private void refreshStats() {
        long activeCount = myDeliveriesList.stream()
            .filter(o -> "out-for-delivery".equals(o.getStatus()))
            .count();
        long completedCount = myDeliveriesList.stream()
            .filter(o -> "delivered".equals(o.getStatus()))
            .count();
        
        // Update stats cards if they exist
        // This would require storing references to the stat cards
    }

    private void updateTotalTips() {
        double total = tipsList.stream()
            .mapToDouble(FileStorage.TipRecord::getAmount)
            .sum();
        if (totalTipsLabel != null) {
            totalTipsLabel.setText("Total Tips: $" + String.format("%.2f", total));
        }
    }

    private Label createStatCard(String label, String value, String color) {
        Label card = new Label(label + "\n" + value);
        card.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 15 20; " +
                     "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;");
        card.setPrefWidth(150);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

