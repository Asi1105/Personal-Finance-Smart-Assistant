package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryExpenseDtoTest {

    @Test
    void testParameterizedConstructorAndGetters() {
        CategoryExpenseDto dto = new CategoryExpenseDto("Food", 100.0, "#FF0000", "50%");

        assertEquals("Food", dto.getCategory());
        assertEquals(100.0, dto.getAmount());
        assertEquals("#FF0000", dto.getColor());
        assertEquals("50%", dto.getPercentage());
    }

    @Test
    void testSetters() {
        CategoryExpenseDto dto = new CategoryExpenseDto(null, null, null, null);

        dto.setCategory("Travel");
        dto.setAmount(200.0);
        dto.setColor("#00FF00");
        dto.setPercentage("25%");

        assertEquals("Travel", dto.getCategory());
        assertEquals(200.0, dto.getAmount());
        assertEquals("#00FF00", dto.getColor());
        assertEquals("25%", dto.getPercentage());
    }
}