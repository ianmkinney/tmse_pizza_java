package com.tmse.pizza.models;

import java.util.List;

// Employee class for employee management
public class Employee {
    private String employeeID;
    private String name;
    private String role;
    private String phone;
    private String password;

    public Employee() {
    }

    public Employee(String employeeID, String name, String role, String phone, String password) {
        this.employeeID = employeeID;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.password = password;
    }

    public boolean login() {
        return false;
    }

    public List<Order> viewAssignedOrders() {
        return null;
    }

    public void updateOrderStatus(String orderID, String status) {
    }

    public String getEmployeeID() { return employeeID; }
    public void setEmployeeID(String employeeID) { this.employeeID = employeeID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

