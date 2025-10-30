package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MonthlyDataDtoTest {

    @Test
    void testDefaultConstructor() {
        MonthlyDataDto dto = new MonthlyDataDto();
        assertNotNull(dto);
        assertNull(dto.getMonth());
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        MonthlyDataDto dto = new MonthlyDataDto("January", 1000.0, 500.0, 500.0);

        assertEquals("January", dto.getMonth());
        assertEquals(1000.0, dto.getIncome());
        assertEquals(500.0, dto.getExpenses());
        assertEquals(500.0, dto.getSavings());
    }

    @Test
    void testSetters() {
        MonthlyDataDto dto = new MonthlyDataDto();

        dto.setMonth("February");
        dto.setIncome(1200.0);
        dto.setExpenses(600.0);
        dto.setSavings(600.0);

        assertEquals("February", dto.getMonth());
        assertEquals(1200.0, dto.getIncome());
        assertEquals(600.0, dto.getExpenses());
        assertEquals(600.0, dto.getSavings());
    }
}