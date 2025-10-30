package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void testDefaultConstructor() {
        UserDto userDto = new UserDto();
        assertNotNull(userDto);
        assertNull(userDto.getId());
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        String id = "1";
        String name = "Test User";
        String email = "test@example.com";
        String createdAt = "2025-10-30";

        UserDto userDto = new UserDto(id, name, email, createdAt);

        assertEquals(id, userDto.getId());
        assertEquals(name, userDto.getName());
        assertEquals(email, userDto.getEmail());
        assertEquals(createdAt, userDto.getCreatedAt());
    }

    @Test
    void testSetters() {
        UserDto userDto = new UserDto();

        userDto.setId("2");
        userDto.setName("New Name");
        userDto.setEmail("new@example.com");
        userDto.setCreatedAt("2025-10-31");

        assertEquals("2", userDto.getId());
        assertEquals("New Name", userDto.getName());
        assertEquals("new@example.com", userDto.getEmail());
        assertEquals("2025-10-31", userDto.getCreatedAt());
    }
}