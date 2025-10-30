package com.sydney.uni.backend.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BudgetTest {

    @Test
    void equalsAndHashCode_coverDataGeneratedBranches() {
        User u = new User();

        Budget a = new Budget();
        a.setId(1L);
        a.setCategory("FOOD_DINING");
        a.setPeriod("monthly");
        a.setAmount(800.0);
        a.setUser(u);

        Budget b = new Budget();
        b.setId(1L);
        b.setCategory("FOOD_DINING");
        b.setPeriod("monthly");
        b.setAmount(800.0);
        b.setUser(u);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setAmount(900.0);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
    }

    @Test
    void testGettersAndSetters() {
        Budget budget = new Budget();
        User user = new User();

        budget.setId(1L);
        budget.setCategory("Food");
        budget.setPeriod("Monthly");
        budget.setAmount(500.0);
        budget.setUser(user);

        assertEquals(1L, budget.getId());
        assertEquals("Food", budget.getCategory());
        assertEquals("Monthly", budget.getPeriod());
        assertEquals(500.0, budget.getAmount());
        assertEquals(user, budget.getUser());
    }

    @Test
    void testLombokGeneratedMethods() {
        Budget budget1 = new Budget();
        budget1.setId(1L);
        budget1.setCategory("Food");

        Budget budget2 = new Budget();
        budget2.setId(1L);
        budget2.setCategory("Food");

        assertEquals(budget1, budget2);
        assertEquals(budget1.hashCode(), budget2.hashCode());
        assertNotNull(budget1.toString());
    }

    @Test
    void testMoreEqualsBranches() {
        Budget a = new Budget();
        Budget b = new Budget();
        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));
        assertEquals(a, b);
        a.setCategory("C");
        assertNotEquals(a, b);
        b.setCategory("C");
        a.setPeriod("M");
        assertNotEquals(a, b);
        b.setPeriod("M");
        a.setAmount(1.0);
        assertNotEquals(a, b);
        b.setAmount(1.0);
        User u = new User();
        a.setUser(u);
        assertNotEquals(a, b);
        b.setUser(u);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}