package com.tmse.pizza.models;

import java.util.List;

/**
 * Represents an item in an order
 */
public class OrderItem {
    private String type;
    private String name;
    private PizzaSize pizzaSize;
    private CrustType crustType;
    private List<String> toppings;
    private BeverageSize beverageSize;
    private String specialInstructions;
    private String cheeseType;
    private String sauceType;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public OrderItem(String type, String name, double unitPrice, int quantity) {
        this.type = type;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = unitPrice * quantity;
    }

    public String getType() { return type; }
    public String getName() { return name; }
    public PizzaSize getPizzaSize() { return pizzaSize; }
    public void setPizzaSize(PizzaSize pizzaSize) { this.pizzaSize = pizzaSize; }
    public CrustType getCrustType() { return crustType; }
    public void setCrustType(CrustType crustType) { this.crustType = crustType; }
    public List<String> getToppings() { return toppings; }
    public void setToppings(List<String> toppings) { this.toppings = toppings; }
    public BeverageSize getBeverageSize() { return beverageSize; }
    public void setBeverageSize(BeverageSize beverageSize) { this.beverageSize = beverageSize; }
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    public String getCheeseType() { return cheeseType; }
    public void setCheeseType(String cheeseType) { this.cheeseType = cheeseType; }
    public String getSauceType() { return sauceType; }
    public void setSauceType(String sauceType) { this.sauceType = sauceType; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }
}

