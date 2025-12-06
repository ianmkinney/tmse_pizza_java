package com.tmse.pizza.models;

/**
 * Payment class matching system design specification
 */
public class Payment {
    private String paymentID;
    private String orderID;
    private String method;
    private double amount;
    private String status;

    public Payment() {
    }

    public Payment(String paymentID, String orderID, String method, double amount, String status) {
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.method = method;
        this.amount = amount;
        this.status = status;
    }

    public boolean processPayment() {
        this.status = "Success";
        return true;
    }

    public boolean validatePayment() {
        return amount > 0 && method != null && !method.isEmpty();
    }

    public boolean retryPayment() {
        return processPayment();
    }

    public String getPaymentID() { return paymentID; }
    public void setPaymentID(String paymentID) { this.paymentID = paymentID; }
    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

