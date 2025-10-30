package com.sydney.uni.backend.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void equalsAndHashCode_coverDataGeneratedBranches() {
        User u = new User();

        Account a = new Account();
        a.setId(1L);
        a.setBalance(100.0);
        a.setSaved(20.0);
        a.setUser(u);

        Account b = new Account();
        b.setId(1L);
        b.setBalance(100.0);
        b.setSaved(20.0);
        b.setUser(u);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setSaved(30.0);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
    }

    @Test
    void testGettersAndSetters() {
        Account account = new Account();
        User user = new User();

        account.setId(1L);
        account.setBalance(1000.0);
        account.setSaved(500.0);
        account.setUser(user);

        assertEquals(1L, account.getId());
        assertEquals(1000.0, account.getBalance());
        assertEquals(500.0, account.getSaved());
        assertEquals(user, account.getUser());
    }

    @Test
    void testLombokGeneratedMethods() {
        Account account1 = new Account();
        account1.setId(1L);
        account1.setBalance(1000.0);

        Account account2 = new Account();
        account2.setId(1L);
        account2.setBalance(1000.0);

        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
        assertNotNull(account1.toString());

        account2.setId(2L);
        assertNotEquals(account1, account2);
    }

    @Test
    void testMoreEqualsBranches() {
        Account a = new Account();
        Account b = new Account();

        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));

        assertEquals(a, b);
        a.setBalance(10.0);
        assertNotEquals(a, b);
        b.setBalance(10.0);
        a.setSaved(5.0);
        assertNotEquals(a, b);
        b.setSaved(5.0);
        User u = new User();
        a.setUser(u);
        assertNotEquals(a, b);
        b.setUser(u);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}