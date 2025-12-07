package com.tmse.pizza.models;

// MenuItem class for menu items
public class MenuItem {
    private String itemID;
    private String name;
    private String category;
    private String description;
    private double price;

    public MenuItem() {
    }

    public MenuItem(String itemID, String name, String category, String description, double price) {
        this.itemID = itemID;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
    }

    public void displayMenu() {
    }

    public MenuItem searchItem(String keyword) {
        return null;
    }

    public void updateMenu() {
    }

    public String getItemID() { return itemID; }
    public void setItemID(String itemID) { this.itemID = itemID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}

