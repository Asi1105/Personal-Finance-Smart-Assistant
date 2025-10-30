package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnsaveMoneyRequestTest {

    @Test
    void testGettersAndSetters() {
        UnsaveMoneyRequest request = new UnsaveMoneyRequest();

        request.setAmount(50.0);
        request.setDescription("Need cash");

        assertEquals(50.0, request.getAmount());
        assertEquals("Need cash", request.getDescription());
    }

    @Test
    void testLombokGeneratedMethods() {
        UnsaveMoneyRequest request1 = new UnsaveMoneyRequest();
        request1.setAmount(50.0);

        UnsaveMoneyRequest request2 = new UnsaveMoneyRequest();
        request2.setAmount(50.0);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    void testEqualsBranches() {
        UnsaveMoneyRequest a = new UnsaveMoneyRequest();
        UnsaveMoneyRequest b = new UnsaveMoneyRequest();

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