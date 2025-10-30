package com.sydney.uni.backend.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SavingLogTest {

    @Test
    void equalsAndHashCode_coverDataGeneratedBranches() {
        User u = new User();
        Account acc = new Account();

        SavingLog a = new SavingLog();
        a.setId(1L);
        a.setAction(SavingAction.SAVE);
        a.setAmount(100.0);
        a.setDescription("x");
        a.setTimestamp(LocalDateTime.now());
        a.setUser(u);
        a.setAccount(acc);

        SavingLog b = new SavingLog();
        b.setId(1L);
        b.setAction(SavingAction.SAVE);
        b.setAmount(100.0);
        b.setDescription("x");
        b.setTimestamp(a.getTimestamp());
        b.setUser(u);
        b.setAccount(acc);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setAction(SavingAction.UNSAVE);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
    }

    @Test
    void testGettersAndSetters() {
        SavingLog log = new SavingLog();
        User user = new User();
        Account account = new Account();
        LocalDateTime time = LocalDateTime.now();

        log.setId(1L);
        log.setAction(SavingAction.SAVE);
        log.setAmount(100.0);
        log.setDescription("Saved");
        log.setTimestamp(time);
        log.setUser(user);
        log.setAccount(account);

        assertEquals(1L, log.getId());
        assertEquals(SavingAction.SAVE, log.getAction());
        assertEquals(100.0, log.getAmount());
        assertEquals("Saved", log.getDescription());
        assertEquals(time, log.getTimestamp());
        assertEquals(user, log.getUser());
        assertEquals(account, log.getAccount());
    }

    @Test
    void testLombokGeneratedMethods() {
        SavingLog log1 = new SavingLog();
        log1.setId(1L);

        SavingLog log2 = new SavingLog();
        log2.setId(1L);

        assertEquals(log1, log2);
        assertEquals(log1.hashCode(), log2.hashCode());
        assertNotNull(log1.toString());
    }

    @Test
    void testMoreEqualsBranches() {
        SavingLog x = new SavingLog();
        SavingLog y = new SavingLog();
        assertTrue(x.equals(x));
        assertFalse(x.equals(null));
        assertFalse(x.equals(new Object()));
        assertEquals(x, y);
        x.setAmount(1.0);
        assertNotEquals(x, y);
        y.setAmount(1.0);
        x.setDescription("d");
        assertNotEquals(x, y);
        y.setDescription("d");
        Account acc = new Account();
        x.setAccount(acc);
        assertNotEquals(x, y);
        y.setAccount(acc);
        assertEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }

    @Test
    void testEquals_NullVsNonNullFields_AllProperties() {
        SavingLog a = new SavingLog();
        SavingLog b = new SavingLog();

        // All null equal
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        // id
        a.setId(2L);
        assertNotEquals(a, b);
        b.setId(2L);
        assertEquals(a, b);

        // action
        a.setAction(SavingAction.SAVE);
        assertNotEquals(a, b);
        b.setAction(SavingAction.SAVE);
        assertEquals(a, b);

        // amount
        a.setAmount(77.0);
        assertNotEquals(a, b);
        b.setAmount(77.0);
        assertEquals(a, b);

        // description
        a.setDescription("desc");
        assertNotEquals(a, b);
        b.setDescription("desc");
        assertEquals(a, b);

        // timestamp
        LocalDateTime t = LocalDateTime.now();
        a.setTimestamp(t);
        assertNotEquals(a, b);
        b.setTimestamp(t);
        assertEquals(a, b);

        // user
        User u = new User();
        a.setUser(u);
        assertNotEquals(a, b);
        b.setUser(u);
        assertEquals(a, b);

        // account
        Account acc = new Account();
        a.setAccount(acc);
        assertNotEquals(a, b);
        b.setAccount(acc);
        assertEquals(a, b);

        // Toggle back to null to hit null vs non-null branches
        a.setDescription(null);
        assertNotEquals(a, b);
        b.setDescription(null);
        assertEquals(a, b);

        a.setUser(null);
        assertNotEquals(a, b);
        b.setUser(null);
        assertEquals(a, b);

        a.setAccount(null);
        assertNotEquals(a, b);
        b.setAccount(null);
        assertEquals(a, b);
    }
}