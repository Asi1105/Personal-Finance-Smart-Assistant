package com.sydney.uni.backend.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void equalsAndHashCode_coverDataGeneratedBranches() {
        Account acc = new Account();

        Transaction a = new Transaction();
        a.setId(1L);
        a.setType(TransactionType.IN);
        a.setDate(LocalDate.now());
        a.setExpenseCategory(null);
        a.setDetail("salary");
        a.setAmount(1000.0);
        a.setNote("note");
        a.setAccount(acc);

        Transaction b = new Transaction();
        b.setId(1L);
        b.setType(TransactionType.IN);
        b.setDate(a.getDate());
        b.setExpenseCategory(null);
        b.setDetail("salary");
        b.setAmount(1000.0);
        b.setNote("note");
        b.setAccount(acc);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setType(TransactionType.OUT);
        b.setExpenseCategory(ExpenseCategory.FOOD_DINING);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
    }

    @Test
    void testGettersAndSetters() {
        Transaction transaction = new Transaction();
        Account account = new Account();
        LocalDate date = LocalDate.now();

        transaction.setId(1L);
        transaction.setType(TransactionType.OUT);
        transaction.setDate(date);
        transaction.setExpenseCategory(ExpenseCategory.FOOD_DINING);
        transaction.setDetail("Groceries");
        transaction.setAmount(150.0);
        transaction.setNote("Weekly shop");
        transaction.setAccount(account);

        assertEquals(1L, transaction.getId());
        assertEquals(TransactionType.OUT, transaction.getType());
        assertEquals(date, transaction.getDate());
        assertEquals(ExpenseCategory.FOOD_DINING, transaction.getExpenseCategory());
        assertEquals("Groceries", transaction.getDetail());
        assertEquals(150.0, transaction.getAmount());
        assertEquals("Weekly shop", transaction.getNote());
        assertEquals(account, transaction.getAccount());
    }

    @Test
    void testLombokGeneratedMethods() {
        Transaction t1 = new Transaction();
        t1.setId(1L);

        Transaction t2 = new Transaction();
        t2.setId(1L);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertNotNull(t1.toString());
    }

    @Test
    void testMoreEqualsBranches() {
        Transaction a = new Transaction();
        Transaction b = new Transaction();
        assertTrue(a.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));
        assertEquals(a, b);
        a.setDetail("d");
        assertNotEquals(a, b);
        b.setDetail("d");
        a.setAmount(5.0);
        assertNotEquals(a, b);
        b.setAmount(5.0);
        a.setNote("n");
        assertNotEquals(a, b);
        b.setNote("n");
        Account acc = new Account();
        a.setAccount(acc);
        assertNotEquals(a, b);
        b.setAccount(acc);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testEquals_NullVsNonNullFields_AllProperties() {
        Transaction a = new Transaction();
        Transaction b = new Transaction();

        // Start equal (all null)
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        // id
        a.setId(10L);
        assertNotEquals(a, b);
        b.setId(10L);
        assertEquals(a, b);

        // type
        a.setType(TransactionType.OUT);
        assertNotEquals(a, b);
        b.setType(TransactionType.OUT);
        assertEquals(a, b);

        // date
        var today = LocalDate.now();
        a.setDate(today);
        assertNotEquals(a, b);
        b.setDate(today);
        assertEquals(a, b);

        // expenseCategory
        a.setExpenseCategory(ExpenseCategory.HEALTHCARE);
        assertNotEquals(a, b);
        b.setExpenseCategory(ExpenseCategory.HEALTHCARE);
        assertEquals(a, b);

        // detail
        a.setDetail("abc");
        assertNotEquals(a, b);
        b.setDetail("abc");
        assertEquals(a, b);

        // amount
        a.setAmount(12.34);
        assertNotEquals(a, b);
        b.setAmount(12.34);
        assertEquals(a, b);

        // note
        a.setNote("memo");
        assertNotEquals(a, b);
        b.setNote("memo");
        assertEquals(a, b);

        // account
        Account acc = new Account();
        a.setAccount(acc);
        assertNotEquals(a, b);
        b.setAccount(acc);
        assertEquals(a, b);

        // Change individual fields back to null one-by-one to cover null vs non-null
        a.setDetail(null);
        assertNotEquals(a, b);
        b.setDetail(null);
        assertEquals(a, b);

        a.setNote(null);
        assertNotEquals(a, b);
        b.setNote(null);
        assertEquals(a, b);

        a.setExpenseCategory(null);
        assertNotEquals(a, b);
        b.setExpenseCategory(null);
        assertEquals(a, b);

        a.setAccount(null);
        assertNotEquals(a, b);
        b.setAccount(null);
        assertEquals(a, b);
    }
}