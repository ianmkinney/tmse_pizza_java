package com.tmse.pizza.models;

import java.util.Date;

// Receipt class for order receipts
public class Receipt {
    private String receiptID;
    private String orderID;
    private String paymentID;
    private Date signatureDate;
    private String signature;

    public Receipt() {
    }

    public Receipt(String receiptID, String orderID, String paymentID, Date signatureDate, String signature) {
        this.receiptID = receiptID;
        this.orderID = orderID;
        this.paymentID = paymentID;
        this.signatureDate = signatureDate;
        this.signature = signature;
    }

    public void generateReceipt() {
        this.signatureDate = new Date();
    }

    public void displayReceipt() {
    }

    public boolean validateReceipt() {
        return receiptID != null && orderID != null && paymentID != null;
    }

    public String getReceiptID() { return receiptID; }
    public void setReceiptID(String receiptID) { this.receiptID = receiptID; }
    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }
    public String getPaymentID() { return paymentID; }
    public void setPaymentID(String paymentID) { this.paymentID = paymentID; }
    public Date getSignatureDate() { return signatureDate; }
    public void setSignatureDate(Date signatureDate) { this.signatureDate = signatureDate; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}

