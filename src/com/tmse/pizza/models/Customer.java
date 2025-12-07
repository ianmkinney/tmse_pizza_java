package com.tmse.pizza.models;

import java.util.List;

// Customer class for customer information
public class Customer {
    private String customerID;
    private String name;
    private String phone;
    private String address;
    private String paymentType;
    private String subscription;

    public Customer() {
    }

    public Customer(String customerID, String name, String phone, String address, String paymentType) {
        this.customerID = customerID;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.paymentType = paymentType;
    }

    public void createAccount() {
    }

    public void updateInfo() {
    }

    public boolean login() {
        return false;
    }

    public List<Order> viewOrders() {
        return null;
    }

    public List<MenuItem> viewMenu() {
        return null;
    }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public String getSubscription() { return subscription; }
    public void setSubscription(String subscription) { this.subscription = subscription; }
}

