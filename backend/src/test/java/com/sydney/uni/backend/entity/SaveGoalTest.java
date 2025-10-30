package com.sydney.uni.backend.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SaveGoalTest {

    @Test
    void testEqualsHashCodeBranches() {
        SaveGoal a = new SaveGoal();
        SaveGoal b = new SaveGoal();

        // reflexive, null, different type
        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));

        // all-null equal
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        // flip fields one by one
        a.setId(1L);
        assertNotEquals(a, b);
        b.setId(1L);

        a.setTargetAmount(1000.0);
        assertNotEquals(a, b);
        b.setTargetAmount(1000.0);

        a.setDescription("desc");
        assertNotEquals(a, b);
        b.setDescription("desc");

        LocalDate due = LocalDate.now();
        a.setDueDate(due);
        assertNotEquals(a, b);
        b.setDueDate(due);

        User u = new User();
        a.setUser(u);
        assertNotEquals(a, b);
        b.setUser(u);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }

    @Test
    void testGettersAndSetters() {
        SaveGoal saveGoal = new SaveGoal();
        User user = new User();
        LocalDate date = LocalDate.now();

        saveGoal.setId(1L);
        saveGoal.setTargetAmount(5000.0);
        saveGoal.setDescription("Holiday");
        saveGoal.setDueDate(date);
        saveGoal.setUser(user);

        assertEquals(1L, saveGoal.getId());
        assertEquals(5000.0, saveGoal.getTargetAmount());
        assertEquals("Holiday", saveGoal.getDescription());
        assertEquals(date, saveGoal.getDueDate());
        assertEquals(user, saveGoal.getUser());
    }

    @Test
    void testLombokGeneratedMethods() {
        SaveGoal goal1 = new SaveGoal();
        goal1.setId(1L);

        SaveGoal goal2 = new SaveGoal();
        goal2.setId(1L);

        assertEquals(goal1, goal2);
        assertEquals(goal1.hashCode(), goal2.hashCode());
        assertNotNull(goal1.toString());
    }
}