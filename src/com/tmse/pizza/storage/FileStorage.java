package com.tmse.pizza.storage;

import com.tmse.pizza.models.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * File-based storage system for users and orders
 */
public class FileStorage {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";
    private static final String ORDERS_FILE = DATA_DIR + "/orders.txt";
    private static final String DELIVERIES_FILE = DATA_DIR + "/deliveries.txt";
    private static final String TIPS_FILE = DATA_DIR + "/tips.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        new File(DATA_DIR).mkdirs();
    }

    public static void saveUser(User user) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            pw.println(user.getUsername() + "|" + user.getPassword() + "|" + user.getRole());
        }
    }

    public static User getUser(String username) throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3 && parts[0].equals(username)) {
                    return new User(parts[0], parts[1], parts[2]);
                }
            }
        }
        return null;
    }

    public static void saveOrder(Order order) throws IOException {
        // Check if order already exists and update it
        List<Order> allOrders = getAllOrders();
        boolean orderExists = false;
        for (Order o : allOrders) {
            if (o.getOrderId().equals(order.getOrderId())) {
                orderExists = true;
                break;
            }
        }
        
        if (orderExists) {
            // Update existing order by rewriting all orders
            updateOrder(order);
        } else {
            // Append new order
            try (PrintWriter pw = new PrintWriter(new FileWriter(ORDERS_FILE, true))) {
                String dateStr = order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : dateFormat.format(new Date());
                pw.println(order.getOrderId() + "|" + order.getUsername() + "|" + 
                          (order.getCustomerName() != null ? order.getCustomerName() : "") + "|" +
                          order.getSubtotal() + "|" + order.getTax() + "|" + order.getTotal() + "|" +
                          order.getStatus() + "|" + dateStr + "|" +
                          (order.getOrderType() != null ? order.getOrderType() : "pickup") + "|" +
                          (order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "") + "|" +
                          (order.getAssignedDriverId() != null ? order.getAssignedDriverId() : "") + "|" +
                          (order.getAssignedDriverName() != null ? order.getAssignedDriverName() : "") + "|" +
                          (order.getPaymentMethod() != null ? order.getPaymentMethod() : "") + "|" +
                          (order.getSpecialInstructions() != null ? order.getSpecialInstructions() : ""));
            }
        }
    }
    
    public static void updateOrder(Order updatedOrder) throws IOException {
        List<Order> allOrders = getAllOrders();
        boolean found = false;
        for (int i = 0; i < allOrders.size(); i++) {
            if (allOrders.get(i).getOrderId().equals(updatedOrder.getOrderId())) {
                allOrders.set(i, updatedOrder);
                found = true;
                break;
            }
        }
        if (found) {
            // Rewrite all orders
            try (PrintWriter pw = new PrintWriter(new FileWriter(ORDERS_FILE, false))) {
                for (Order order : allOrders) {
                    String dateStr = order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : dateFormat.format(new Date());
                    pw.println(order.getOrderId() + "|" + order.getUsername() + "|" + 
                              (order.getCustomerName() != null ? order.getCustomerName() : "") + "|" +
                              order.getSubtotal() + "|" + order.getTax() + "|" + order.getTotal() + "|" +
                              order.getStatus() + "|" + dateStr + "|" +
                              (order.getOrderType() != null ? order.getOrderType() : "pickup") + "|" +
                              (order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "") + "|" +
                              (order.getAssignedDriverId() != null ? order.getAssignedDriverId() : "") + "|" +
                              (order.getAssignedDriverName() != null ? order.getAssignedDriverName() : "") + "|" +
                              (order.getPaymentMethod() != null ? order.getPaymentMethod() : "") + "|" +
                              (order.getSpecialInstructions() != null ? order.getSpecialInstructions() : ""));
                }
            }
        }
    }

    public static List<Order> getOrdersByUser(String username) throws IOException {
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return orders;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[1].equals(username)) {
                    Order order = parseOrderFromLine(parts);
                    orders.add(order);
                }
            }
        }
        return orders;
    }
    
    public static List<Order> getAllOrders() throws IOException {
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return orders;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    Order order = parseOrderFromLine(parts);
                    orders.add(order);
                }
            }
        }
        return orders;
    }
    
    private static Order parseOrderFromLine(String[] parts) {
        Order order = new Order(parts[0], parts[1]);
        if (parts.length > 2 && !parts[2].isEmpty()) order.setCustomerName(parts[2]);
        if (parts.length > 3 && !parts[3].isEmpty()) order.setSubtotal(Double.parseDouble(parts[3]));
        if (parts.length > 4 && !parts[4].isEmpty()) order.setTax(Double.parseDouble(parts[4]));
        if (parts.length > 5 && !parts[5].isEmpty()) order.setTotalAmount(Double.parseDouble(parts[5]));
        if (parts.length > 6 && !parts[6].isEmpty()) order.setStatus(parts[6]);
        if (parts.length > 7 && !parts[7].isEmpty()) {
            try {
                order.setOrderDate(dateFormat.parse(parts[7]));
            } catch (ParseException e) {
                order.setOrderDate(new Date());
            }
        }
        if (parts.length > 8 && !parts[8].isEmpty()) order.setOrderType(parts[8]);
        if (parts.length > 9 && !parts[9].isEmpty()) order.setDeliveryAddress(parts[9]);
        if (parts.length > 10 && !parts[10].isEmpty()) order.setAssignedDriverId(parts[10]);
        if (parts.length > 11 && !parts[11].isEmpty()) order.setAssignedDriverName(parts[11]);
        if (parts.length > 12 && !parts[12].isEmpty()) order.setPaymentMethod(parts[12]);
        if (parts.length > 13 && !parts[13].isEmpty()) order.setSpecialInstructions(parts[13]);
        return order;
    }
    
    public static List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
        }
        return users;
    }
    
    public static List<Order> getOrdersByDriver(String driverId) throws IOException {
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return orders;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length > 10 && parts[10].equals(driverId)) {
                    Order order = parseOrderFromLine(parts);
                    orders.add(order);
                }
            }
        }
        return orders;
    }
    
    public static List<Order> getAvailableDeliveryOrders() throws IOException {
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return orders;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length > 8 && "delivery".equals(parts[8])) {
                    // Only include orders that are ready and not yet assigned
                    if (parts.length > 6) {
                        String status = parts[6].toLowerCase();
                        if ("ready".equals(status) || "preparing".equals(status)) {
                            if (parts.length <= 10 || parts[10].isEmpty()) {
                                Order order = parseOrderFromLine(parts);
                                orders.add(order);
                            }
                        }
                    }
                }
            }
        }
        return orders;
    }
    
    public static void saveTip(String orderId, String driverId, double tipAmount) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TIPS_FILE, true))) {
            pw.println(orderId + "|" + driverId + "|" + tipAmount + "|" + dateFormat.format(new Date()));
        }
    }
    
    public static List<TipRecord> getTipsByDriver(String driverId) throws IOException {
        List<TipRecord> tips = new ArrayList<>();
        File file = new File(TIPS_FILE);
        if (!file.exists()) return tips;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[1].equals(driverId)) {
                    TipRecord tip = new TipRecord(parts[0], parts[1], Double.parseDouble(parts[2]), 
                                                  parts.length > 3 ? parts[3] : "");
                    tips.add(tip);
                }
            }
        }
        return tips;
    }
    
    public static class TipRecord {
        private String orderId;
        private String driverId;
        private double amount;
        private String date;
        
        public TipRecord(String orderId, String driverId, double amount, String date) {
            this.orderId = orderId;
            this.driverId = driverId;
            this.amount = amount;
            this.date = date;
        }
        
        public String getOrderId() { return orderId; }
        public String getDriverId() { return driverId; }
        public double getAmount() { return amount; }
        public String getDate() { return date; }
    }

    public static void initializeDefaultUsers() throws IOException {
        // Ensure default users exist
        User[] defaultUsers = {
            new User("customer", "password123", "customer"),
            new User("admin", "admin123", "admin"),
            new User("driver", "driver123", "driver")
        };

        for (User defaultUser : defaultUsers) {
            User existing = getUser(defaultUser.getUsername());
            if (existing == null) {
                saveUser(defaultUser);
            }
        }
    }
}

