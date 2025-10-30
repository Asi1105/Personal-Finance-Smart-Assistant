package com.sydney.uni.backend.dto;

// Import the enums from the entity package
import com.sydney.uni.backend.entity.ExpenseCategory;
import com.sydney.uni.backend.entity.TransactionType;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TransactionDtoTest {

    @Test
    void testGettersAndSetters() {
        TransactionDto dto = new TransactionDto();
        LocalDate date = LocalDate.now();

        // 1. Test Setters
        dto.setId(1L);
        dto.setType(TransactionType.OUT);
        dto.setDate(date);
        dto.setExpenseCategory(ExpenseCategory.FOOD_DINING);
        dto.setDetail("Groceries");
        dto.setAmount(150.0);
        dto.setNote("Weekly shopping");
        dto.setCategoryDisplayName("Food");
        dto.setIcon("🍎");

        // 2. Test Getters
        assertEquals(1L, dto.getId());
        assertEquals(TransactionType.OUT, dto.getType());
        assertEquals(date, dto.getDate());
        assertEquals(ExpenseCategory.FOOD_DINING, dto.getExpenseCategory());
        assertEquals("Groceries", dto.getDetail());
        assertEquals(150.0, dto.getAmount());
        assertEquals("Weekly shopping", dto.getNote());
        assertEquals("Food", dto.getCategoryDisplayName());
        assertEquals("🍎", dto.getIcon());
    }

    @Test
    void testLombokGeneratedMethods() {
        // Test equals(), hashCode(), and toString()
        LocalDate date = LocalDate.now();

        TransactionDto dto1 = new TransactionDto();
        dto1.setId(1L);
        dto1.setDetail("Groceries");
        dto1.setDate(date);
        dto1.setType(TransactionType.OUT);

        TransactionDto dto2 = new TransactionDto();
        dto2.setId(1L);
        dto2.setDetail("Groceries");
        dto2.setDate(date);
        dto2.setType(TransactionType.OUT);

        // Test equals()
        assertEquals(dto1, dto2);

        // Test hashCode()
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // Test toString()
        assertNotNull(dto1.toString());

        // Test not equals
        dto2.setId(2L);
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testEqualsBranches() {
        TransactionDto a = new TransactionDto();
        TransactionDto b = new TransactionDto();

        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        a.setDetail("x");
        assertNotEquals(a, b);

        b.setDetail("x");
        b.setIcon("i");
        assertNotEquals(a, b);

        // more fields combinations
        a = new TransactionDto();
        b = new TransactionDto();
        a.setId(1L);
        b.setId(1L);
        a.setAmount(10.0);
        b.setAmount(10.0);
        a.setNote("n");
        assertNotEquals(a, b);
        b.setNote("n");
        a.setCategoryDisplayName("cd");
        b.setCategoryDisplayName("cd");
        a.setIcon("ic");
        b.setIcon("ic");
        a.setExpenseCategory(ExpenseCategory.OTHER);
        b.setExpenseCategory(ExpenseCategory.OTHER);
        a.setType(TransactionType.OUT);
        assertNotEquals(a, b);
        b.setType(TransactionType.OUT);
        LocalDate date = LocalDate.now();
        a.setDate(date);
        assertNotEquals(a, b);
        b.setDate(date);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}