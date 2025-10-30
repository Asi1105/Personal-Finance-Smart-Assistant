package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void testDefaultConstructor() {
        AuthResponse response = new AuthResponse();
        assertNotNull(response);
        assertNull(response.getUser());
        assertNull(response.getToken());
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        UserDto user = new UserDto("1", "Test", "test@test.com", "now");
        String token = "my.token";

        AuthResponse response = new AuthResponse(user, token);

        assertEquals(user, response.getUser());
        assertEquals(token, response.getToken());
    }

    @Test
    void testSetters() {
        AuthResponse response = new AuthResponse();
        UserDto user = new UserDto();
        String token = "my.token";

        response.setUser(user);
        response.setToken(token);

        assertEquals(user, response.getUser());
        assertEquals(token, response.getToken());
    }
}