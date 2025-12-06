package com.tmse.pizza.models;

import java.util.List;

/**
 * Admin class matching system design specification
 */
public class Admin {
    private String adminID;
    private String username;
    private String password;

    public Admin() {
    }

    public Admin(String adminID, String username, String password) {
        this.adminID = adminID;
        this.username = username;
        this.password = password;
    }

    public void manageMenu() {
    }

    public void manageEmployees() {
    }

    public Report generateReport() {
        return null;
    }

    public List<Logs> viewSystemLogs() {
        return null;
    }

    public String getAdminID() { return adminID; }
    public void setAdminID(String adminID) { this.adminID = adminID; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

