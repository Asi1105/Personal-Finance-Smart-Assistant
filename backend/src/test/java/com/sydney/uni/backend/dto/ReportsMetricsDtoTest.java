package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReportsMetricsDtoTest {

    @Test
    void testDefaultConstructor() {
        ReportsMetricsDto dto = new ReportsMetricsDto();
        assertNotNull(dto);
        assertNull(dto.getTotalIncome());
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        ReportsMetricsDto dto = new ReportsMetricsDto(1000.0, 500.0, 100.0, 500.0, 0.5);

        assertEquals(1000.0, dto.getTotalIncome());
        assertEquals(500.0, dto.getTotalExpenses());
        assertEquals(100.0, dto.getTotalSavings());
        assertEquals(500.0, dto.getAvgMonthlyExpenses());
        assertEquals(0.5, dto.getSavingsRate());
    }

    @Test
    void testSetters() {
        ReportsMetricsDto dto = new ReportsMetricsDto();

        dto.setTotalIncome(2000.0);
        dto.setTotalExpenses(800.0);
        dto.setTotalSavings(200.0);
        dto.setAvgMonthlyExpenses(800.0);
        dto.setSavingsRate(0.6);

        assertEquals(2000.0, dto.getTotalIncome());
        assertEquals(800.0, dto.getTotalExpenses());
        assertEquals(200.0, dto.getTotalSavings());
        assertEquals(800.0, dto.getAvgMonthlyExpenses());
        assertEquals(0.6, dto.getSavingsRate());
    }
}