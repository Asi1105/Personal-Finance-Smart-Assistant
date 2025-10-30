package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ReportsDtoTest {

    @Test
    void testDefaultConstructor() {
        ReportsDto dto = new ReportsDto();
        assertNotNull(dto);
        assertNull(dto.getMetrics());
    }

    @Test
    void testParameterizedConstructorAndGetters() {
        List<MonthlyDataDto> monthlyData = Collections.emptyList();
        List<CategoryExpenseDto> categoryExpenses = Collections.emptyList();
        List<BudgetComparisonDto> budgetComparison = Collections.emptyList();
        ReportsMetricsDto metrics = new ReportsMetricsDto();

        ReportsDto dto = new ReportsDto(monthlyData, categoryExpenses, budgetComparison, metrics);

        assertEquals(monthlyData, dto.getMonthlyData());
        assertEquals(categoryExpenses, dto.getCategoryExpenses());
        assertEquals(budgetComparison, dto.getBudgetComparison());
        assertEquals(metrics, dto.getMetrics());
    }

    @Test
    void testSetters() {
        ReportsDto dto = new ReportsDto();
        List<MonthlyDataDto> monthlyData = Collections.singletonList(new MonthlyDataDto());
        List<CategoryExpenseDto> categoryExpenses = Collections.singletonList(new CategoryExpenseDto(null,null,null,null));
        List<BudgetComparisonDto> budgetComparison = Collections.singletonList(new BudgetComparisonDto());
        ReportsMetricsDto metrics = new ReportsMetricsDto();

        dto.setMonthlyData(monthlyData);
        dto.setCategoryExpenses(categoryExpenses);
        dto.setBudgetComparison(budgetComparison);
        dto.setMetrics(metrics);

        assertEquals(monthlyData, dto.getMonthlyData());
        assertEquals(categoryExpenses, dto.getCategoryExpenses());
        assertEquals(budgetComparison, dto.getBudgetComparison());
        assertEquals(metrics, dto.getMetrics());
    }
}