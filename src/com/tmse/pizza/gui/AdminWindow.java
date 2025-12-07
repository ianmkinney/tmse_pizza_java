package com.tmse.pizza.gui;

import com.tmse.pizza.models.*;
import com.tmse.pizza.storage.FileStorage;
import javafx.application.Platform;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Admin Dashboard Window
 * Allows admin to view all orders, generate reports, manage users, and assign drivers
 */
public class AdminWindow {
    private Stage stage;
    private User currentUser;
    private ObservableList<Order> ordersList;
    private ObservableList<User> usersList;
    private TableView<Order> ordersTable;
    private TableView<User> usersTable;
    private Label revenueLabel;
    private Label pendingLabel;
    private Label preparingLabel;
    private Label readyLabel;

    public AdminWindow(Stage primaryStage, User user) {
        this.stage = primaryStage;
        this.currentUser = user;
        this.ordersList = FXCollections.observableArrayList();
        this.usersList = FXCollections.observableArrayList();
    }

    public void show() {
        stage.setTitle("TMSE Pizza - Admin Dashboard");
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f9fafb;");

        // Header
        HBox headerBox = new HBox(20);
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #dc2626, #f59e0b); -fx-padding: 15 20;");
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label headerLabel = new Label("Admin Dashboard");
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

        // Orders Tab
        Tab ordersTab = new Tab("Orders");
        ordersTab.setClosable(false);
        ordersTab.setContent(createOrdersTab());
        
        // Users Tab
        Tab usersTab = new Tab("Users");
        usersTab.setClosable(false);
        usersTab.setContent(createUsersTab());
        
        // Reports Tab
        Tab reportsTab = new Tab("Reports");
        reportsTab.setClosable(false);
        reportsTab.setContent(createReportsTab());

        tabPane.getTabs().addAll(ordersTab, usersTab, reportsTab);
        root.setCenter(tabPane);

        // Footer
        VBox footerBox = CommonLayout.createFooter();
        root.setBottom(footerBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // Full screen is already set at startup, no need to toggle

        // Load data
        refreshOrders();
        refreshUsers();
        updateStats();
    }

    private VBox createOrdersTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        // Stats cards
        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER_LEFT);

        pendingLabel = createStatCard("Pending", "0", "#fbbf24");
        preparingLabel = createStatCard("Preparing", "0", "#3b82f6");
        readyLabel = createStatCard("Ready", "0", "#10b981");
        revenueLabel = createStatCard("Today's Revenue", "$0.00", "#dc2626");

        statsBox.getChildren().addAll(pendingLabel, preparingLabel, readyLabel, revenueLabel);

        // Filter buttons
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Button allButton = new Button("All");
        Button pendingButton = new Button("Pending");
        Button preparingButton = new Button("Preparing");
        Button readyButton = new Button("Ready");
        Button outForDeliveryButton = new Button("Out for Delivery");
        Button deliveredButton = new Button("Delivered");
        Button cancelledButton = new Button("Cancelled");

        allButton.setOnAction(e -> filterOrders("all"));
        pendingButton.setOnAction(e -> filterOrders("Pending"));
        preparingButton.setOnAction(e -> filterOrders("Preparing"));
        readyButton.setOnAction(e -> filterOrders("ready"));
        outForDeliveryButton.setOnAction(e -> filterOrders("out-for-delivery"));
        deliveredButton.setOnAction(e -> filterOrders("delivered"));
        cancelledButton.setOnAction(e -> filterOrders("cancelled"));

        filterBox.getChildren().addAll(allButton, pendingButton, preparingButton, readyButton, 
                                       outForDeliveryButton, deliveredButton, cancelledButton);

        // Orders table
        ordersTable = new TableView<>();
        ordersTable.setItems(ordersList);
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Order, String> orderIdCol = new TableColumn<>("Order #");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                order.getCustomerName() != null ? order.getCustomerName() : order.getUsername()
            );
        });

        TableColumn<Order, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            String type = order.getOrderType() != null && order.getOrderType().equals("delivery") 
                ? "Delivery" : "Pickup";
            return new javafx.beans.property.SimpleStringProperty(type);
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

        TableColumn<Order, String> driverCol = new TableColumn<>("Driver");
        driverCol.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                order.getAssignedDriverName() != null ? order.getAssignedDriverName() : "Unassigned"
            );
        });

        TableColumn<Order, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<Order, Void>() {
            private final Button statusButton = new Button();
            private final ComboBox<String> driverCombo = new ComboBox<>();
            private final Button cancelButton = new Button("Cancel");

            {
                statusButton.setStyle("-fx-padding: 5 10; -fx-font-size: 12px;");
                cancelButton.setStyle("-fx-padding: 5 10; -fx-font-size: 12px; -fx-background-color: #ef4444; -fx-text-fill: white;");
                
                statusButton.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    handleStatusUpdate(order);
                });
                
                cancelButton.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    String status = order.getStatus();
                    if (status == null) status = "pending";
                    status = status.toLowerCase();
                    if ("delivered".equals(status) || "cancelled".equals(status)) {
                        showAlert("Cannot cancel this order");
                        return;
                    }
                    order.setStatus("cancelled");
                    try {
                        FileStorage.updateOrder(order);
                        refreshOrders();
                    } catch (IOException ex) {
                        showAlert("Error updating order: " + ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(5);
                    
                    // Status button
                    String status = order.getStatus();
                    if (status == null) status = "pending";
                    status = status.toLowerCase();
                    if ("pending".equals(status)) {
                        statusButton.setText("Start Preparing");
                        statusButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 5 10;");
                        box.getChildren().add(statusButton);
                    } else if ("preparing".equals(status)) {
                        statusButton.setText("Mark Ready");
                        statusButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 5 10;");
                        box.getChildren().add(statusButton);
                    } else if ("ready".equals(status) && "delivery".equals(order.getOrderType())) {
                        // Show driver assignment
                        try {
                            List<User> drivers = FileStorage.getAllUsers().stream()
                                .filter(u -> "driver".equals(u.getRole()))
                                .collect(Collectors.toList());
                            driverCombo.getItems().clear();
                            driverCombo.getItems().add("Select Driver");
                            for (User driver : drivers) {
                                driverCombo.getItems().add(driver.getUsername());
                            }
                            driverCombo.setOnAction(e -> {
                                String selected = driverCombo.getSelectionModel().getSelectedItem();
                                if (selected != null && !selected.equals("Select Driver")) {
                                    order.setAssignedDriverId(selected);
                                    order.setAssignedDriverName(selected);
                                    order.setStatus("out-for-delivery");
                                    try {
                                        FileStorage.updateOrder(order);
                                        refreshOrders();
                                    } catch (IOException ex) {
                                        showAlert("Error assigning driver: " + ex.getMessage());
                                    }
                                }
                            });
                            box.getChildren().add(driverCombo);
                        } catch (IOException ex) {
                            showAlert("Error loading drivers: " + ex.getMessage());
                        }
                    } else if ("ready".equals(status) && (order.getOrderType() == null || "pickup".equals(order.getOrderType()))) {
                        statusButton.setText("Picked Up");
                        statusButton.setStyle("-fx-background-color: #6b7280; -fx-text-fill: white; -fx-padding: 5 10;");
                        box.getChildren().add(statusButton);
                    }
                    
                    if (!"delivered".equals(status) && !"cancelled".equals(status)) {
                        box.getChildren().add(cancelButton);
                    }
                    
                    setGraphic(box);
                }
            }
        });

        ordersTable.getColumns().addAll(orderIdCol, customerCol, typeCol, itemsCol, totalCol, statusCol, driverCol, actionsCol);

        ScrollPane scrollPane = new ScrollPane(ordersTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        vbox.getChildren().addAll(statsBox, filterBox, scrollPane);
        return vbox;
    }

    private void handleStatusUpdate(Order order) {
        String currentStatus = order.getStatus();
        if (currentStatus == null) currentStatus = "pending";
        currentStatus = currentStatus.toLowerCase();
        if ("pending".equals(currentStatus)) {
            order.setStatus("preparing");
        } else if ("preparing".equals(currentStatus)) {
            order.setStatus("ready");
        } else if ("ready".equals(currentStatus) && (order.getOrderType() == null || "pickup".equals(order.getOrderType()))) {
            order.setStatus("delivered");
        }
        
        try {
            FileStorage.updateOrder(order);
            refreshOrders();
        } catch (IOException ex) {
            showAlert("Error updating order: " + ex.getMessage());
        }
    }

    private VBox createUsersTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label titleLabel = new Label("All Users");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        usersTable = new TableView<>();
        usersTable.setItems(usersList);
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        usersTable.getColumns().addAll(usernameCol, roleCol);

        ScrollPane scrollPane = new ScrollPane(usersTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        vbox.getChildren().addAll(titleLabel, scrollPane);
        return vbox;
    }

    private VBox createReportsTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label titleLabel = new Label("Generate Reports");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button dailyReportButton = new Button("Generate Daily Summary Report");
        dailyReportButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14px;");
        dailyReportButton.setOnAction(e -> generateDailyReport());

        Button salesReportButton = new Button("Generate Sales Report");
        salesReportButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14px;");
        salesReportButton.setOnAction(e -> generateSalesReport());

        Button resetSalesButton = new Button("Reset Sales for Today");
        resetSalesButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14px; -fx-font-weight: bold;");
        resetSalesButton.setOnAction(e -> resetSalesForToday());

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefRowCount(20);
        reportArea.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane(reportArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        vbox.getChildren().addAll(titleLabel, dailyReportButton, salesReportButton, resetSalesButton, scrollPane);
        
        // Store reference to report area for updates
        this.reportArea = reportArea;
        
        return vbox;
    }

    private TextArea reportArea;

    private void generateDailyReport() {
        try {
            List<Order> allOrders = FileStorage.getAllOrders();
            Date today = new Date();
            java.util.Calendar todayCal = java.util.Calendar.getInstance();
            todayCal.setTime(today);
            int todayYear = todayCal.get(java.util.Calendar.YEAR);
            int todayMonth = todayCal.get(java.util.Calendar.MONTH);
            int todayDay = todayCal.get(java.util.Calendar.DAY_OF_MONTH);
            
            List<Order> todayOrders = allOrders.stream()
                .filter(o -> {
                    if (o.getOrderDate() == null) return false;
                    java.util.Calendar orderCal = java.util.Calendar.getInstance();
                    orderCal.setTime(o.getOrderDate());
                    return orderCal.get(java.util.Calendar.YEAR) == todayYear &&
                           orderCal.get(java.util.Calendar.MONTH) == todayMonth &&
                           orderCal.get(java.util.Calendar.DAY_OF_MONTH) == todayDay;
                })
                .collect(Collectors.toList());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String todayStr = dateFormat.format(today);

            int totalOrders = todayOrders.size();
            double totalSales = todayOrders.stream()
                .filter(o -> !"cancelled".equals(o.getStatus()))
                .mapToDouble(Order::getTotal)
                .sum();
            double totalTax = todayOrders.stream()
                .filter(o -> !"cancelled".equals(o.getStatus()))
                .mapToDouble(Order::getTax)
                .sum();
            double avgOrderValue = totalOrders > 0 ? totalSales / totalOrders : 0;

            // Count payment methods
            double cashPayments = todayOrders.stream()
                .filter(o -> o.getPaymentMethod() != null && o.getPaymentMethod().toLowerCase().contains("cash"))
                .mapToDouble(Order::getTotal)
                .sum();
            double cardPayments = totalSales - cashPayments;

            // Top selling items
            Map<String, Integer> itemCounts = new HashMap<>();
            for (Order order : todayOrders) {
                for (OrderItem item : order.getItems()) {
                    String itemName = item.getName();
                    itemCounts.put(itemName, itemCounts.getOrDefault(itemName, 0) + item.getQuantity());
                }
            }

            // Order type breakdown
            long deliveryCount = todayOrders.stream()
                .filter(o -> "delivery".equals(o.getOrderType()))
                .count();
            long pickupCount = todayOrders.stream()
                .filter(o -> "pickup".equals(o.getOrderType()) || o.getOrderType() == null)
                .count();

            // Refunds/Voids
            List<Order> refunds = todayOrders.stream()
                .filter(o -> "cancelled".equals(o.getStatus()))
                .collect(Collectors.toList());

            StringBuilder report = new StringBuilder();
            report.append("TMSE Pizza — Daily Summary Report\n");
            report.append("Date: ").append(todayStr).append("\n");
            report.append("==========================================\n\n");
            report.append("Sales Summary\n");
            report.append("- Total Orders: ").append(totalOrders).append("\n");
            report.append("- Total Sales: $").append(String.format("%.2f", totalSales)).append("\n");
            report.append("- Total Tax Collected: $").append(String.format("%.2f", totalTax)).append("\n");
            report.append("- Average Order Value: $").append(String.format("%.2f", avgOrderValue)).append("\n");
            report.append("- Cash Payments: $").append(String.format("%.2f", cashPayments)).append("\n");
            report.append("- Card Payments: $").append(String.format("%.2f", cardPayments)).append("\n\n");

            report.append("Top-Selling Items\n");
            itemCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(entry -> 
                    report.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n")
                );
            report.append("\n");

            report.append("Order Type Breakdown\n");
            report.append("- Delivery: ").append(deliveryCount).append("\n");
            report.append("- Pickup: ").append(pickupCount).append("\n\n");

            if (!refunds.isEmpty()) {
                report.append("Refunds / Voids\n");
                for (Order refund : refunds) {
                    report.append("- ").append(refund.getOrderId())
                          .append(" — $").append(String.format("%.2f", refund.getTotal())).append("\n");
                }
            }

            reportArea.setText(report.toString());
        } catch (IOException ex) {
            showAlert("Error generating report: " + ex.getMessage());
        }
    }

    private void generateSalesReport() {
        try {
            List<Order> allOrders = FileStorage.getAllOrders();
            
            double totalSales = allOrders.stream()
                .filter(o -> !"cancelled".equals(o.getStatus()))
                .mapToDouble(Order::getTotal)
                .sum();
            
            double totalTax = allOrders.stream()
                .filter(o -> !"cancelled".equals(o.getStatus()))
                .mapToDouble(Order::getTax)
                .sum();

            long totalOrders = allOrders.stream()
                .filter(o -> !"cancelled".equals(o.getStatus()))
                .count();

            StringBuilder report = new StringBuilder();
            report.append("TMSE Pizza — Sales Report\n");
            report.append("==========================================\n\n");
            report.append("Total Orders: ").append(totalOrders).append("\n");
            report.append("Total Sales: $").append(String.format("%.2f", totalSales)).append("\n");
            report.append("Total Tax: $").append(String.format("%.2f", totalTax)).append("\n");
            report.append("Net Revenue: $").append(String.format("%.2f", totalSales - totalTax)).append("\n");

            reportArea.setText(report.toString());
        } catch (IOException ex) {
            showAlert("Error generating report: " + ex.getMessage());
        }
    }

    private void resetSalesForToday() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Reset Sales");
        confirmAlert.setHeaderText("Reset Sales for Today");
        confirmAlert.setContentText("This will delete all orders from today. This action cannot be undone.\n\nAre you sure you want to continue?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    List<Order> allOrders = FileStorage.getAllOrders();
                    Date today = new Date();
                    java.util.Calendar todayCal = java.util.Calendar.getInstance();
                    todayCal.setTime(today);
                    int todayYear = todayCal.get(java.util.Calendar.YEAR);
                    int todayMonth = todayCal.get(java.util.Calendar.MONTH);
                    int todayDay = todayCal.get(java.util.Calendar.DAY_OF_MONTH);
                    
                    // Filter out today's orders
                    List<Order> ordersToKeep = allOrders.stream()
                        .filter(o -> {
                            if (o.getOrderDate() == null) return true;
                            java.util.Calendar orderCal = java.util.Calendar.getInstance();
                            orderCal.setTime(o.getOrderDate());
                            return !(orderCal.get(java.util.Calendar.YEAR) == todayYear &&
                                   orderCal.get(java.util.Calendar.MONTH) == todayMonth &&
                                   orderCal.get(java.util.Calendar.DAY_OF_MONTH) == todayDay);
                        })
                        .collect(Collectors.toList());
                    
                    // Delete all orders and re-save only the ones to keep
                    java.io.File ordersFile = new java.io.File("data/orders.txt");
                    if (ordersFile.exists()) {
                        ordersFile.delete();
                    }
                    
                    // Re-save all orders except today's
                    for (Order order : ordersToKeep) {
                        FileStorage.saveOrder(order);
                    }
                    
                    showAlert("Sales for today have been reset successfully!");
                    refreshOrders();
                    if (reportArea != null) {
                        reportArea.clear();
                    }
                } catch (IOException ex) {
                    showAlert("Error resetting sales: " + ex.getMessage());
                }
            }
        });
    }

    private Label createStatCard(String label, String value, String color) {
        Label card = new Label(label + "\n" + value);
        card.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 15 20; " +
                     "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;");
        card.setPrefWidth(150);
        card.setAlignment(Pos.CENTER);
        return card;
    }

    private void filterOrders(String status) {
        try {
            List<Order> allOrders = FileStorage.getAllOrders();
            if ("all".equals(status)) {
                ordersList.setAll(allOrders);
            } else {
                List<Order> filtered = allOrders.stream()
                    .filter(o -> {
                        String orderStatus = o.getStatus();
                        if (orderStatus == null) return false;
                        // Handle case-insensitive matching and status variations
                        if ("ready".equals(status)) {
                            return "ready".equalsIgnoreCase(orderStatus);
                        } else if ("out-for-delivery".equals(status)) {
                            return "out-for-delivery".equalsIgnoreCase(orderStatus);
                        } else {
                            return status.equalsIgnoreCase(orderStatus);
                        }
                    })
                    .collect(Collectors.toList());
                ordersList.setAll(filtered);
            }
        } catch (IOException ex) {
            showAlert("Error loading orders: " + ex.getMessage());
        }
    }

    private void refreshOrders() {
        try {
            List<Order> allOrders = FileStorage.getAllOrders();
            ordersList.setAll(allOrders);
            updateStats();
        } catch (IOException ex) {
            showAlert("Error loading orders: " + ex.getMessage());
        }
    }
    
    private void updateStats() {
        try {
            List<Order> allOrders = FileStorage.getAllOrders();
            
            // Count orders by status
            long pendingCount = allOrders.stream()
                .filter(o -> "pending".equalsIgnoreCase(o.getStatus()))
                .count();
            long preparingCount = allOrders.stream()
                .filter(o -> "preparing".equalsIgnoreCase(o.getStatus()))
                .count();
            long readyCount = allOrders.stream()
                .filter(o -> "ready".equalsIgnoreCase(o.getStatus()))
                .count();
            
            // Calculate today's revenue
            Date today = new Date();
            java.util.Calendar todayCal = java.util.Calendar.getInstance();
            todayCal.setTime(today);
            int todayYear = todayCal.get(java.util.Calendar.YEAR);
            int todayMonth = todayCal.get(java.util.Calendar.MONTH);
            int todayDay = todayCal.get(java.util.Calendar.DAY_OF_MONTH);
            
            double todayRevenue = allOrders.stream()
                .filter(o -> {
                    if (o.getOrderDate() == null) return false;
                    java.util.Calendar orderCal = java.util.Calendar.getInstance();
                    orderCal.setTime(o.getOrderDate());
                    return orderCal.get(java.util.Calendar.YEAR) == todayYear &&
                           orderCal.get(java.util.Calendar.MONTH) == todayMonth &&
                           orderCal.get(java.util.Calendar.DAY_OF_MONTH) == todayDay;
                })
                .filter(o -> !"cancelled".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(Order::getTotal)
                .sum();
            
            // Update stat cards
            if (pendingLabel != null) {
                pendingLabel.setText("Pending\n" + pendingCount);
            }
            if (preparingLabel != null) {
                preparingLabel.setText("Preparing\n" + preparingCount);
            }
            if (readyLabel != null) {
                readyLabel.setText("Ready\n" + readyCount);
            }
            if (revenueLabel != null) {
                revenueLabel.setText("Today's Revenue\n$" + String.format("%.2f", todayRevenue));
            }
        } catch (IOException ex) {
            showAlert("Error updating stats: " + ex.getMessage());
        }
    }

    private void refreshUsers() {
        try {
            List<User> allUsers = FileStorage.getAllUsers();
            usersList.setAll(allUsers);
        } catch (IOException ex) {
            showAlert("Error loading users: " + ex.getMessage());
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

