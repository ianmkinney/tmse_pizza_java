package com.tmse.pizza.models;

/**
 * Pizza size enumeration
 */
public enum PizzaSize {
    PERSONAL("Personal (8\")", 0.0),
    SMALL("Small (10\")", 2.0),
    MEDIUM("Medium (12\")", 4.0),
    LARGE("Large (16\")", 6.0);

    private final String label;
    private final double price;

    PizzaSize(String label, double price) {
        this.label = label;
        this.price = price;
    }

    public String getLabel() { return label; }
    public double getPrice() { return price; }
}

