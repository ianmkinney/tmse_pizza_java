package com.tmse.pizza.models;

/**
 * Crust type enumeration
 */
public enum CrustType {
    HAND_TOSSED("Hand-Tossed"),
    THIN_NINJA("Thin Ninja Style"),
    DEEP_DISH("Deep Dish"),
    OOZE_FILLED("Ooze Filled Crust (Provolone Cheese Filled Crust)");

    private final String label;

    CrustType(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }
}

