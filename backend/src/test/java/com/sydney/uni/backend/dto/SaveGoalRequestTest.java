package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SaveGoalRequestTest {

    @Test
    void testGettersAndSetters() {
        SaveGoalRequest request = new SaveGoalRequest();

        request.setTargetAmount(5000.0);
        request.setDescription("Holiday");

        assertEquals(5000.0, request.getTargetAmount());
        assertEquals("Holiday", request.getDescription());
    }

    @Test
    void testLombokGeneratedMethods() {
        SaveGoalRequest request1 = new SaveGoalRequest();
        request1.setTargetAmount(5000.0);
        request1.setDescription("Holiday");

        SaveGoalRequest request2 = new SaveGoalRequest();
        request2.setTargetAmount(5000.0);
        request2.setDescription("Holiday");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
    }

    @Test
    void testEqualsBranches() {
        SaveGoalRequest a = new SaveGoalRequest();
        SaveGoalRequest b = new SaveGoalRequest();

        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        a.setTargetAmount(1.0);
        assertNotEquals(a, b);

        b.setTargetAmount(1.0);
        b.setDescription("d");
        assertNotEquals(a, b);
    }
}