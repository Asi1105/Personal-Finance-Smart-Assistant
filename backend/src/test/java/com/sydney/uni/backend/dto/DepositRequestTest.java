package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DepositRequestTest {

    @Test
    void testGettersAndSetters() {
        DepositRequest request = new DepositRequest();

        request.setAmount(100.50);
        request.setDescription("Test Deposit");

        assertEquals(100.50, request.getAmount());
        assertEquals("Test Deposit", request.getDescription());
    }

    @Test
    void testLombokGeneratedMethods() {
        DepositRequest request1 = new DepositRequest();
        request1.setAmount(100.0);
        request1.setDescription("Test");

        DepositRequest request2 = new DepositRequest();
        request2.setAmount(100.0);
        request2.setDescription("Test");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    void testEqualsBranches() {
        DepositRequest a = new DepositRequest();
        DepositRequest b = new DepositRequest();

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
    }
}