package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void testDefaultConstructor() {
        RegisterRequest request = new RegisterRequest();
        assertNotNull(request);
        assertNull(request.getName());
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "pass123");

        assertEquals("Test User", request.getName());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("pass123", request.getPassword());
    }

    @Test
    void testSetters() {
        RegisterRequest request = new RegisterRequest();

        request.setName("New User");
        request.setEmail("new@example.com");
        request.setPassword("newPass");

        assertEquals("New User", request.getName());
        assertEquals("new@example.com", request.getEmail());
        assertEquals("newPass", request.getPassword());
    }
}