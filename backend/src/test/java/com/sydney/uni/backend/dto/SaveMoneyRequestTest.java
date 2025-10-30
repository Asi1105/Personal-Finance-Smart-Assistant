package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SaveMoneyRequestTest {

    @Test
    void testGettersAndSetters() {
        SaveMoneyRequest request = new SaveMoneyRequest();

        request.setAmount(200.0);
        request.setDescription("For savings");

        assertEquals(200.0, request.getAmount());
        assertEquals("For savings", request.getDescription());
    }

    @Test
    void testLombokGeneratedMethods() {
        SaveMoneyRequest request1 = new SaveMoneyRequest();
        request1.setAmount(200.0);

        SaveMoneyRequest request2 = new SaveMoneyRequest();
        request2.setAmount(200.0);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    void testEqualsBranches() {
        SaveMoneyRequest a = new SaveMoneyRequest();
        SaveMoneyRequest b = new SaveMoneyRequest();

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