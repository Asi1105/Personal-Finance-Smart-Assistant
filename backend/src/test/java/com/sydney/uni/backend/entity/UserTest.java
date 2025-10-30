package com.sydney.uni.backend.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructor_shouldInitializeFields() {
        User u = new User("Alice", "a@example.com", "pwd");
        assertEquals("Alice", u.getName());
        assertEquals("a@example.com", u.getEmail());
        assertNotNull(u.getCreatedAt());
        assertNull(u.getId());
        u.setPassword("P2");
        assertEquals("P2", u.getPassword());
    }

    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNull(user.getId());
    }

    @Test
    void testParameterizedConstructor() {
        User user = new User("Test User", "test@example.com", "password123");
        assertNotNull(user);
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        LocalDateTime time = LocalDateTime.now();
        user.setId(1L);
        user.setName("New Name");
        user.setEmail("new@example.com");
        user.setPassword("newPass");
        user.setCreatedAt(time);
        assertEquals(1L, user.getId());
        assertEquals("New Name", user.getName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newPass", user.getPassword());
        assertEquals(time, user.getCreatedAt());
    }
}