package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ExpenseRequestTest {

    @Test
    void testGettersAndSetters() {
        ExpenseRequest request = new ExpenseRequest();
        LocalDate date = LocalDate.now();

        request.setDescription("Lunch");
        request.setCategory("Food & Dining");
        request.setAmount(25.50);
        request.setDate(date);
        request.setNotes("With colleagues");

        assertEquals("Lunch", request.getDescription());
        assertEquals("Food & Dining", request.getCategory());
        assertEquals(25.50, request.getAmount());
        assertEquals(date, request.getDate());
        assertEquals("With colleagues", request.getNotes());
    }

    @Test
    void testLombokGeneratedMethods() {
        LocalDate date = LocalDate.now();

        ExpenseRequest request1 = new ExpenseRequest();
        request1.setCategory("Food");
        request1.setAmount(25.50);
        request1.setDate(date);

        ExpenseRequest request2 = new ExpenseRequest();
        request2.setCategory("Food");
        request2.setAmount(25.50);
        request2.setDate(date);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    void testEqualsBranches() {
        ExpenseRequest a = new ExpenseRequest();
        ExpenseRequest b = new ExpenseRequest();

        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        a.setAmount(1.0);
        assertNotEquals(a, b);

        b.setAmount(1.0);
        b.setDescription("d");
        assertNotEquals(a, b);

        a.setDescription("d");
        a.setCategory("Food");
        assertNotEquals(a, b);
        b.setCategory("Food");

        LocalDate date = LocalDate.now();
        a.setDate(date);
        assertNotEquals(a, b);
        b.setDate(date);

        a.setNotes("n");
        assertNotEquals(a, b);
        b.setNotes("n");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}