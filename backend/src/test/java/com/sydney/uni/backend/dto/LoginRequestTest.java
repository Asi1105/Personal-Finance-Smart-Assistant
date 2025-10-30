package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testDefaultConstructor() {
        LoginRequest request = new LoginRequest();
        assertNotNull(request);
        assertNull(request.getEmail());
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        LoginRequest request = new LoginRequest("test@example.com", "pass123");

        assertEquals("test@example.com", request.getEmail());
        assertEquals("pass123", request.getPassword());
    }

    @Test
    void testSetters() {
        LoginRequest request = new LoginRequest();

        request.setEmail("new@example.com");
        request.setPassword("newPass");

        assertEquals("new@example.com", request.getEmail());
        assertEquals("newPass", request.getPassword());
    }
}