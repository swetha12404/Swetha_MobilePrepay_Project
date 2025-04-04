package com.example.productcrud.model;

public class AuthRequest {
    private String username; // Username for admins, mobile for subscribers
    private String password; // Required for admins, optional for subscribers

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}