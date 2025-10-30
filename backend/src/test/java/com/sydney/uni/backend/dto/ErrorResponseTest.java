package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testDefaultConstructor() {
        ErrorResponse error = new ErrorResponse();
        assertNotNull(error);
        assertNull(error.getMessage());
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        Map<String, Object> details = new HashMap<>();
        details.put("field", "email");

        ErrorResponse error = new ErrorResponse("Invalid email", "INVALID_EMAIL", details);

        assertEquals("Invalid email", error.getMessage());
        assertEquals("INVALID_EMAIL", error.getCode());
        assertEquals(details, error.getDetails());
    }

    @Test
    void testSetters() {
        ErrorResponse error = new ErrorResponse();
        Map<String, Object> details = new HashMap<>();

        error.setMessage("New message");
        error.setCode("NEW_CODE");
        error.setDetails(details);

        assertEquals("New message", error.getMessage());
        assertEquals("NEW_CODE", error.getCode());
        assertEquals(details, error.getDetails());
    }
}