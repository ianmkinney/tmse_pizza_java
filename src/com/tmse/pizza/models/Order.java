package com.tmse.pizza.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Order class for managing customer orders
public class Order {
    private String orderID;
    private String customerID;
    private String customerName;
    private Date orderDate;
    private double totalAmount;
    private String status;
    private List<OrderItem> items;
    private double subtotal;
    private double tax;
    private String orderType; // "delivery" or "pickup"
    private String deliveryAddress;
    private String assignedDriverId;
    private String assignedDriverName;
    private String paymentMethod;
    private String specialInstructions;

    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = new Date();
        this.status = "pending";
    }

    public Order(String orderID, String customerID) {
        this();
        this.orderID = orderID;
        this.customerID = customerID;
    }

    public void addItem(MenuItem item) {
        OrderItem orderItem = new OrderItem("pizza", item.getName(), item.getPrice(), 1);
        items.add(orderItem);
        calculateTotals();
    }

    public void addItem(OrderItem item) {
        items.add(item);
        calculateTotals();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        calculateTotals();
    }

    public void updateQuantity(String itemID, int qty) {
        for (OrderItem item : items) {
            if (item.getName().equals(itemID)) {
                item = new OrderItem(item.getType(), item.getName(), item.getUnitPrice(), qty);
                break;
            }
        }
        calculateTotals();
    }

    public List<MenuItem> viewCart() {
        return null;
    }

    public boolean confirmOrder() {
        this.status = "pending";
        return true;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    private void calculateTotals() {
        subtotal = items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
        tax = subtotal * 0.08;
        totalAmount = subtotal + tax;
    }

    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }
    public String getOrderId() { return orderID; }
    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }
    public String getUsername() { return customerID; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getTotal() { return totalAmount; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getAssignedDriverId() { return assignedDriverId; }
    public void setAssignedDriverId(String assignedDriverId) { this.assignedDriverId = assignedDriverId; }
    public String getAssignedDriverName() { return assignedDriverName; }
    public void setAssignedDriverName(String assignedDriverName) { this.assignedDriverName = assignedDriverName; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
}

