package com.paulo.nader.demo;

/**
 * Represents a user with an identifier and an assigned role.
 */
public class User {
    private String userId;
    private UserRole role;

    /**
     * Creates a user with the specified identifier and role.
     *
     * @param userId the user's identifier
     * @param role the user's role
     */
    public User(String userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }

    /**
     * Returns this user's identifier.
     *
     * @return the user identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns this user's role.
     *
     * @return the user role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Sets this user's role.
     *
     * @param role the new user role
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    
}
