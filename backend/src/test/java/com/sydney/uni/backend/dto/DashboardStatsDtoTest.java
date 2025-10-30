package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DashboardStatsDtoTest {

    @Test
    void testGettersAndSetters() {
        DashboardStatsDto dto = new DashboardStatsDto();

        dto.setTotalBalance(10000.0);
        dto.setSaved(2000.0);
        dto.setMonthlySpending(1500.0);
        dto.setBudgetLeft(500.0);
        dto.setSavingsGoal(10000.0);
        dto.setSavingsProgress(20.0);
        dto.setBudgetUsedPercentage(75.0);
        dto.setMonthlySpendingChange(10.0);
        dto.setLastMonthSpending(1350.0);
        dto.setHasSavingsGoal(true);

        assertEquals(10000.0, dto.getTotalBalance());
        assertEquals(2000.0, dto.getSaved());
        assertEquals(1500.0, dto.getMonthlySpending());
        assertEquals(500.0, dto.getBudgetLeft());
        assertEquals(10000.0, dto.getSavingsGoal());
        assertEquals(20.0, dto.getSavingsProgress());
        assertEquals(75.0, dto.getBudgetUsedPercentage());
        assertEquals(10.0, dto.getMonthlySpendingChange());
        assertEquals(1350.0, dto.getLastMonthSpending());
        assertTrue(dto.getHasSavingsGoal());
    }

    @Test
    void testEqualsAndHashCodeAndToString() {
        DashboardStatsDto dto1 = new DashboardStatsDto();
        DashboardStatsDto dto2 = new DashboardStatsDto();

        // baseline equality
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertTrue(dto1.toString().contains("DashboardStatsDto"));
        assertTrue(dto1.equals(dto1)); // self equality
        assertFalse(dto1.equals(null)); // null comparison
        assertFalse(dto1.equals(new Object())); // different type
    }

    @Test
    void testEqualsDifferentFields() {
        DashboardStatsDto base = new DashboardStatsDto();
        base.setTotalBalance(1.0);
        base.setSaved(2.0);
        base.setMonthlySpending(3.0);
        base.setBudgetLeft(4.0);
        base.setSavingsGoal(5.0);
        base.setSavingsProgress(6.0);
        base.setBudgetUsedPercentage(7.0);
        base.setMonthlySpendingChange(8.0);
        base.setLastMonthSpending(9.0);
        base.setHasSavingsGoal(true);

        DashboardStatsDto diff = new DashboardStatsDto();
        diff.setTotalBalance(1.0);
        diff.setSaved(2.0);
        diff.setMonthlySpending(3.0);
        diff.setBudgetLeft(4.0);
        diff.setSavingsGoal(5.0);
        diff.setSavingsProgress(6.0);
        diff.setBudgetUsedPercentage(7.0);
        diff.setMonthlySpendingChange(8.0);
        diff.setLastMonthSpending(9.0);
        diff.setHasSavingsGoal(true);

        assertEquals(base, diff); // identical values

        // one-by-one inequality tests
        diff.setTotalBalance(999.0);
        assertNotEquals(base, diff);
        diff.setTotalBalance(1.0);

        diff.setSaved(999.0);
        assertNotEquals(base, diff);
        diff.setSaved(2.0);

        diff.setMonthlySpending(999.0);
        assertNotEquals(base, diff);
        diff.setMonthlySpending(3.0);

        diff.setBudgetLeft(999.0);
        assertNotEquals(base, diff);
        diff.setBudgetLeft(4.0);

        diff.setSavingsGoal(999.0);
        assertNotEquals(base, diff);
        diff.setSavingsGoal(5.0);

        diff.setSavingsProgress(999.0);
        assertNotEquals(base, diff);
        diff.setSavingsProgress(6.0);

        diff.setBudgetUsedPercentage(999.0);
        assertNotEquals(base, diff);
        diff.setBudgetUsedPercentage(7.0);

        diff.setMonthlySpendingChange(999.0);
        assertNotEquals(base, diff);
        diff.setMonthlySpendingChange(8.0);

        diff.setLastMonthSpending(999.0);
        assertNotEquals(base, diff);
        diff.setLastMonthSpending(9.0);

        diff.setHasSavingsGoal(false);
        assertNotEquals(base, diff);
        diff.setHasSavingsGoal(true);

        // back to equality again
        assertEquals(base, diff);
        assertEquals(base.hashCode(), diff.hashCode());
    }
}
