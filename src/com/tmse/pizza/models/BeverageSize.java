package com.tmse.pizza.models;

/**
 * Beverage size enumeration
 */
public enum BeverageSize {
    SMALL("Small (12 oz)"),
    MEDIUM("Medium (20 oz)"),
    LARGE("Large (32 oz)");

    private final String label;

    BeverageSize(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }
}

