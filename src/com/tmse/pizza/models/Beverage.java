package com.tmse.pizza.models;

/**
 * Represents a beverage item
 */
public class Beverage {
    private String id;
    private String name;
    private String description;
    private double smallPrice;
    private double mediumPrice;
    private double largePrice;

    public Beverage(String id, String name, String description, double smallPrice, double mediumPrice, double largePrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.smallPrice = smallPrice;
        this.mediumPrice = mediumPrice;
        this.largePrice = largePrice;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice(BeverageSize size) {
        switch (size) {
            case SMALL:
                return smallPrice;
            case MEDIUM:
                return mediumPrice;
            case LARGE:
                return largePrice;
            default:
                return smallPrice;
        }
    }
}

