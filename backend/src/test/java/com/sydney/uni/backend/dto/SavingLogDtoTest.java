package com.sydney.uni.backend.dto;

import com.sydney.uni.backend.entity.SavingAction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SavingLogDtoTest {

    @Test
    void equalsAndHashCode_coverDataGeneratedBranches() {
        SavingLogDto a = new SavingLogDto();
        a.setId(1L);
        a.setAction(SavingAction.SAVE);
        a.setAmount(100.0);
        a.setDescription("desc");
        a.setTimestamp(LocalDateTime.now());
        a.setActionDisplayName("Money Saved");
        a.setIcon("💰");

        SavingLogDto b = new SavingLogDto();
        b.setId(1L);
        b.setAction(SavingAction.SAVE);
        b.setAmount(100.0);
        b.setDescription("desc");
        b.setTimestamp(a.getTimestamp());
        b.setActionDisplayName("Money Saved");
        b.setIcon("💰");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setAmount(200.0);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
    }

    @Test
    void testGettersAndSetters() {
        SavingLogDto dto = new SavingLogDto();
        LocalDateTime time = LocalDateTime.now();

        dto.setId(1L);
        dto.setAction(SavingAction.SAVE);
        dto.setAmount(100.0);
        dto.setDescription("Saved money");
        dto.setTimestamp(time);
        dto.setActionDisplayName("Money Saved");
        dto.setIcon("💰");

        assertEquals(1L, dto.getId());
        assertEquals(SavingAction.SAVE, dto.getAction());
        assertEquals(100.0, dto.getAmount());
        assertEquals("Saved money", dto.getDescription());
        assertEquals(time, dto.getTimestamp());
        assertEquals("Money Saved", dto.getActionDisplayName());
        assertEquals("💰", dto.getIcon());
    }

    @Test
    void testLombokGeneratedMethods() {
        SavingLogDto dto1 = new SavingLogDto();
        dto1.setId(1L);
        dto1.setAction(SavingAction.SAVE);

        SavingLogDto dto2 = new SavingLogDto();
        dto2.setId(1L);
        dto2.setAction(SavingAction.SAVE);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }

    @Test
    void testMoreEqualsBranches() {
        // all-null equals
        SavingLogDto x = new SavingLogDto();
        SavingLogDto y = new SavingLogDto();
        assertEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());

        // flip each field to break equality then restore
        x.setId(1L);
        assertNotEquals(x, y);
        y.setId(1L);

        x.setAction(SavingAction.SAVE);
        assertNotEquals(x, y);
        y.setAction(SavingAction.SAVE);

        x.setAmount(10.0);
        assertNotEquals(x, y);
        y.setAmount(10.0);

        x.setDescription("d");
        assertNotEquals(x, y);
        y.setDescription("d");

        var t = LocalDateTime.now();
        x.setTimestamp(t);
        assertNotEquals(x, y);
        y.setTimestamp(t);

        x.setActionDisplayName("n");
        assertNotEquals(x, y);
        y.setActionDisplayName("n");

        x.setIcon("i");
        assertNotEquals(x, y);
        y.setIcon("i");

        assertEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }
}