package com.paulo.nader.demo;

/**
 * User
 */
public class User {
    private String userId;
    private UserRole role;

    public User(String userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }
    public String getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }

    
}
