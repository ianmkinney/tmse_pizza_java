package com.tmse.pizza.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Menu data initialization
 */
public class MenuData {
    private static List<Topping> toppings;
    private static List<Pizza> pizzas;
    private static List<Beverage> beverages;

    static {
        initializeToppings();
        initializePizzas();
        initializeBeverages();
    }

    private static void initializeToppings() {
        toppings = Arrays.asList(
            new Topping("pepperoni", "Pepperoni", 1.50),
            new Topping("sausage", "Italian Sausage", 1.50),
            new Topping("mushrooms", "Fresh Mushrooms", 1.25),
            new Topping("onions", "Onions", 1.00),
            new Topping("bell-peppers", "Bell Peppers", 1.00),
            new Topping("olives", "Black Olives", 1.25),
            new Topping("bacon", "Crispy Bacon", 1.75),
            new Topping("extra-cheese", "Extra Cheese", 1.50)
        );
    }

    private static void initializePizzas() {
        pizzas = Arrays.asList(
            new Pizza("cowabunga-classic", "Cowabunga Classic",
                "The ultimate TMSE classic with crispy pepperoni and melty mozzarella",
                8.99, Arrays.asList("pepperoni", "extra-cheese")),
            new Pizza("shredder-supreme", "Shredder Supreme",
                "A villainously loaded feast stacked with sausage, mushroom and green peppers",
                11.99, Arrays.asList("sausage", "mushrooms", "bell-peppers")),
            new Pizza("mutant-veggie-melt", "Mutant Veggie Melt",
                "A vibrant mutation of flavor with fresh spinach, red peppers, mushrooms, and black olives",
                9.99, Arrays.asList("mushrooms", "bell-peppers", "olives")),
            new Pizza("ninja-chicken", "Ninja Chicken Combo",
                "A stealthy blend of bold flavors featuring grilled chicken, smoky bacon, and fresh red onions",
                11.99, Arrays.asList("bacon", "onions")),
            new Pizza("splinters-wisdom", "Splinter's Wisdom",
                "A masterful blend of simplicity with alfredo base topped with ricotta, mozzarella and a hint of parmesan",
                8.99, Arrays.asList("extra-cheese")),
            new Pizza("build-your-own", "Build Your Own",
                "Start with our classic cheese and add up to 4 toppings of your choice",
                7.99, new ArrayList<>())
        );
    }

    private static void initializeBeverages() {
        beverages = Arrays.asList(
            new Beverage("mutant-ooze", "Mutant Ooze",
                "A glowing green concoction of lime soda, pineapple juice, and a splash of coconut",
                1.99, 2.49, 2.99),
            new Beverage("turtle-juice", "Turtle Juice",
                "A refreshing green blend packed with ninja energy. Fresh spinach, kiwi, and green apple",
                1.99, 2.49, 2.99),
            new Beverage("rockin-rhino-rootbeer", "Rockin Rhino Rootbeer",
                "A hard-hitting, frothy classic!! This holds a bold vanilla and sassafras flavor with a creamy foam top!",
                2.99, 3.49, 3.99),
            new Beverage("fruit-ninja", "Fruit Ninja",
                "A slice-tastic explosion of fruit flavor. A blend of strawberries, mango, pineapple, and orange juice",
                2.99, 3.49, 3.99),
            new Beverage("ninja-water", "Ninja Water",
                "Pure refreshing bottled water",
                1.49, 1.99, 2.49)
        );
    }

    public static List<Topping> getToppings() { return new ArrayList<>(toppings); }
    public static List<Pizza> getPizzas() { return new ArrayList<>(pizzas); }
    public static List<Beverage> getBeverages() { return new ArrayList<>(beverages); }

    public static Topping getToppingById(String id) {
        return toppings.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }

    public static Pizza getPizzaById(String id) {
        return pizzas.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public static Beverage getBeverageById(String id) {
        return beverages.stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
    }
}

