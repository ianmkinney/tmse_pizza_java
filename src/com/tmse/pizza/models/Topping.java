package com.tmse.pizza.models;

// Represents a pizza topping
public class Topping {
    private String id;
    private String name;
    private double price;

    public Topping(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}

