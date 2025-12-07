package com.tmse.pizza.models;

import java.util.ArrayList;
import java.util.List;

// Represents a pizza item
public class Pizza {
    private String id;
    private String name;
    private String description;
    private double basePrice;
    private List<String> defaultToppings;

    public Pizza(String id, String name, String description, double basePrice, List<String> defaultToppings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.defaultToppings = defaultToppings != null ? defaultToppings : new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getBasePrice() { return basePrice; }
    public List<String> getDefaultToppings() { return defaultToppings; }
}

