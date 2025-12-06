package com.tmse.pizza.models;

/**
 * Delivery class matching system design specification
 */
public class Delivery {
    private String deliveryID;
    private String orderID;
    private String deliveryAddress;
    private String deliveryStatus;

    public Delivery() {
    }

    public Delivery(String deliveryID, String orderID, String deliveryAddress, String deliveryStatus) {
        this.deliveryID = deliveryID;
        this.orderID = orderID;
        this.deliveryAddress = deliveryAddress;
        this.deliveryStatus = deliveryStatus;
    }

    public void assignOrder(String driverID) {
    }

    public void updateStatus(String status) {
        this.deliveryStatus = status;
    }

    public boolean confirmDelivery() {
        this.deliveryStatus = "Completed";
        return true;
    }

    public String getDeliveryID() { return deliveryID; }
    public void setDeliveryID(String deliveryID) { this.deliveryID = deliveryID; }
    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
}

