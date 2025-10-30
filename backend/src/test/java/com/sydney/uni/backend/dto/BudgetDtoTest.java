package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BudgetDtoTest {

    @Test
    void testGettersAndSetters() {
        BudgetDto dto = new BudgetDto();

        dto.setId(1L);
        dto.setCategory("Food");
        dto.setPeriod("Monthly");
        dto.setAmount(500.0);
        dto.setSpent(250.0);
        dto.setRemaining(250.0);
        dto.setUtilizationPercentage(50.0);

        assertEquals(1L, dto.getId());
        assertEquals("Food", dto.getCategory());
        assertEquals("Monthly", dto.getPeriod());
        assertEquals(500.0, dto.getAmount());
        assertEquals(250.0, dto.getSpent());
        assertEquals(250.0, dto.getRemaining());
        assertEquals(50.0, dto.getUtilizationPercentage());
    }

    @Test
    void testLombokGeneratedMethods() {
        BudgetDto dto1 = new BudgetDto();
        dto1.setId(1L);
        dto1.setCategory("Food");

        BudgetDto dto2 = new BudgetDto();
        dto2.setId(1L);
        dto2.setCategory("Food");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());

        dto2.setId(2L);
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testEqualsBranches() {
        BudgetDto a = new BudgetDto();
        BudgetDto b = new BudgetDto();

        // self
        assertTrue(a.equals(a));
        // null and different type
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));

        // both all-null should be equal
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        // single-field difference
        a.setCategory("A");
        assertNotEquals(a, b);
        // multi-field difference
        b.setCategory("A");
        b.setPeriod("M");
        assertNotEquals(a, b);

        // more fields impact equality/hash
        a = new BudgetDto();
        b = new BudgetDto();
        a.setId(1L);
        assertNotEquals(a, b);
        b.setId(1L);
        a.setAmount(100.0);
        b.setAmount(100.0);
        assertEquals(a, b);
        a.setSpent(10.0);
        assertNotEquals(a, b);
        b.setSpent(10.0);
        b.setRemaining(90.0);
        assertNotEquals(a, b);
        a.setRemaining(90.0);
        a.setUtilizationPercentage(10.0);
        assertNotEquals(a, b);
        b.setUtilizationPercentage(10.0);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}