package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BudgetComparisonDtoTest {

    @Test
    void testDefaultConstructor() {
        BudgetComparisonDto dto = new BudgetComparisonDto();
        assertNotNull(dto);
        assertNull(dto.getCategory());
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        BudgetComparisonDto dto = new BudgetComparisonDto("Food", 500.0, 300.0, 200.0);

        assertEquals("Food", dto.getCategory());
        assertEquals(500.0, dto.getBudgeted());
        assertEquals(300.0, dto.getSpent());
        assertEquals(200.0, dto.getRemaining());
    }

    @Test
    void testSetters() {
        BudgetComparisonDto dto = new BudgetComparisonDto();

        dto.setCategory("Travel");
        dto.setBudgeted(1000.0);
        dto.setSpent(100.0);
        dto.setRemaining(900.0);

        assertEquals("Travel", dto.getCategory());
        assertEquals(1000.0, dto.getBudgeted());
        assertEquals(100.0, dto.getSpent());
        assertEquals(900.0, dto.getRemaining());
    }
}