
package com.paulo.nader.demo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("user1", UserRole.USER);
    }

    @Test
    public void testConstructor() {
        assertEquals("user1", user.getUserId());
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    public void testGetUserId() {
        assertEquals("user1", user.getUserId());
    }

    @Test
    public void testGetRole() {
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    public void testSetRole() {
        user.setRole(UserRole.ADMIN);

        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    public void testSetRoleBackToUser() {
        user.setRole(UserRole.ADMIN);
        user.setRole(UserRole.USER);

        assertEquals(UserRole.USER, user.getRole());
    }
}
