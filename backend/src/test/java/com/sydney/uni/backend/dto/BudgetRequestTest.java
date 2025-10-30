package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BudgetRequestTest {

    @Test
    void testGettersAndSetters() {
        BudgetRequest request = new BudgetRequest();

        request.setCategory("Utilities");
        request.setAmount(150.0);
        request.setPeriod("Monthly");

        assertEquals("Utilities", request.getCategory());
        assertEquals(150.0, request.getAmount());
        assertEquals("Monthly", request.getPeriod());
    }

    @Test
    void testLombokGeneratedMethods() {
        BudgetRequest request1 = new BudgetRequest();
        request1.setCategory("Utilities");
        request1.setAmount(150.0);

        BudgetRequest request2 = new BudgetRequest();
        request2.setCategory("Utilities");
        request2.setAmount(150.0);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    void testEqualsBranches() {
        BudgetRequest a = new BudgetRequest();
        BudgetRequest b = new BudgetRequest();

        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        a.setAmount(1.0);
        assertNotEquals(a, b);

        b.setAmount(1.0);
        b.setCategory("C");
        assertNotEquals(a, b);

        a.setCategory("C");
        a.setPeriod("Monthly");
        assertNotEquals(a, b);
        b.setPeriod("Monthly");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}