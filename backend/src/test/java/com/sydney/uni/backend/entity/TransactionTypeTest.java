package com.sydney.uni.backend.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTypeTest {

    @Test
    void testEnumValues() {
        TransactionType[] types = TransactionType.values();
        assertEquals(2, types.length);
        assertEquals(TransactionType.IN, types[0]);
        assertEquals(TransactionType.OUT, types[1]);
    }

    @Test
    void testEnumValueOf() {
        TransactionType in = TransactionType.valueOf("IN");
        assertEquals(TransactionType.IN, in);

        TransactionType out = TransactionType.valueOf("OUT");
        assertEquals(TransactionType.OUT, out);
    }
}