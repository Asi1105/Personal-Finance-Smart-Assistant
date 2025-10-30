package com.sydney.uni.backend.service;

import com.sydney.uni.backend.dto.*;
import com.sydney.uni.backend.entity.*;
import com.sydney.uni.backend.repository.*;
import com.sydney.uni.backend.services.ReportsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private SavingLogRepository savingLogRepository;

    @InjectMocks
    private ReportsService reportsService;

    private Transaction transactionIn;
    private Transaction transactionOut;
    private Budget budget;
    private SavingLog savingLog;

    @BeforeEach
    void setUp() {
        transactionIn = new Transaction();
        transactionIn.setType(TransactionType.IN);
        transactionIn.setAmount(1000.0);
        transactionIn.setDate(LocalDate.now());
        transactionIn.setExpenseCategory(ExpenseCategory.OTHER);

        transactionOut = new Transaction();
        transactionOut.setType(TransactionType.OUT);
        transactionOut.setAmount(500.0);
        transactionOut.setDate(LocalDate.now());
        transactionOut.setExpenseCategory(ExpenseCategory.FOOD_DINING);

        budget = new Budget();
        budget.setCategory("FOOD_DINING");
        budget.setAmount(600.0);

        savingLog = new SavingLog();
        savingLog.setAction(SavingAction.SAVE);
        savingLog.setAmount(200.0);
        savingLog.setTimestamp(LocalDateTime.now());
    }

    @Test
    void testGetReportsData_Success() {
        when(transactionRepository.findByUserIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(List.of(transactionIn, transactionOut));
        when(budgetRepository.findByUserId(anyLong()))
                .thenReturn(List.of(budget));
        when(savingLogRepository.findByUserIdOrderByTimestampDesc(anyLong()))
                .thenReturn(List.of(savingLog));

        ReportsDto reports = reportsService.getReportsData(1L, "6months");

        assertNotNull(reports);
        assertNotNull(reports.getMonthlyData());
        assertFalse(reports.getCategoryExpenses().isEmpty());
        assertFalse(reports.getBudgetComparison().isEmpty());
        assertNotNull(reports.getMetrics());
        assertTrue(reports.getMetrics().getTotalIncome() > 0);
    }

    @Test
    void testCalculateStartDate() {
        LocalDate now = LocalDate.now();
        LocalDate sixMonthsAgo = now.minusMonths(6).withDayOfMonth(1);
        LocalDate yearStart = now.withDayOfYear(1);

        LocalDate result6 = invokeCalculateStartDate("6months");
        LocalDate resultYear = invokeCalculateStartDate("year");
        LocalDate resultDefault = invokeCalculateStartDate("unknown");

        assertEquals(sixMonthsAgo, result6);
        assertEquals(yearStart, resultYear);
        assertEquals(sixMonthsAgo, resultDefault);
    }

    private LocalDate invokeCalculateStartDate(String period) {
        try {
            var method = ReportsService.class.getDeclaredMethod("calculateStartDate", LocalDate.class, String.class);
            method.setAccessible(true);
            return (LocalDate) method.invoke(reportsService, LocalDate.now(), period);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateCategoryExpenses_EmptyTransactions() {
        when(transactionRepository.findByUserIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(budgetRepository.findByUserId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(savingLogRepository.findByUserIdOrderByTimestampDesc(anyLong()))
                .thenReturn(Collections.emptyList());

        ReportsDto reports = reportsService.getReportsData(1L, "3months");

        assertNotNull(reports);
        assertNotNull(reports.getCategoryExpenses());
        assertTrue(reports.getCategoryExpenses().isEmpty());
    }

    @Test
    void testGetReportsData_NoData() {
        when(transactionRepository.findByUserIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(budgetRepository.findByUserId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(savingLogRepository.findByUserIdOrderByTimestampDesc(anyLong()))
                .thenReturn(Collections.emptyList());

        ReportsDto reports = reportsService.getReportsData(1L, "6months");

        assertNotNull(reports);
        assertNotNull(reports.getMonthlyData());
        assertNotNull(reports.getBudgetComparison());
        assertNotNull(reports.getMetrics());
    }

    @Test
    void testMonthlyData_SixMonthsWindowSize() {
        // no data, but period = 6months should still yield exactly 6 months in list
        when(transactionRepository.findByUserIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(budgetRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());
        when(savingLogRepository.findByUserIdOrderByTimestampDesc(anyLong())).thenReturn(Collections.emptyList());

        ReportsDto dto = reportsService.getReportsData(1L, "6months");
        assertEquals(6, dto.getMonthlyData().size());
    }

    @Test
    void testSavingLogs_UnsaveSubtracts() {
        when(transactionRepository.findByUserIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(budgetRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());

        SavingLog save = new SavingLog();
        save.setAction(SavingAction.SAVE);
        save.setAmount(200.0);
        save.setTimestamp(LocalDateTime.now());
        SavingLog unsave = new SavingLog();
        unsave.setAction(SavingAction.UNSAVE);
        unsave.setAmount(50.0);
        unsave.setTimestamp(LocalDateTime.now());
        when(savingLogRepository.findByUserIdOrderByTimestampDesc(anyLong()))
                .thenReturn(List.of(save, unsave));

        ReportsDto dto = reportsService.getReportsData(1L, "6months");
        double sum = dto.getMonthlyData().stream().mapToDouble(MonthlyDataDto::getSavings).sum();
        assertEquals(150.0, sum, 0.0001);
    }

    @Test
    void testBudgetComparison_PeriodMonthsMultiply() {
        // one budget and one OUT transaction in its category
        Transaction out = new Transaction();
        out.setType(TransactionType.OUT);
        out.setAmount(100.0);
        out.setDate(LocalDate.now());
        out.setExpenseCategory(ExpenseCategory.HEALTHCARE);
        when(transactionRepository.findByUserIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(List.of(out));

        Budget b = new Budget();
        b.setCategory("HEALTHCARE");
        b.setAmount(50.0);
        when(budgetRepository.findByUserId(anyLong())).thenReturn(List.of(b));
        when(savingLogRepository.findByUserIdOrderByTimestampDesc(anyLong())).thenReturn(Collections.emptyList());

        ReportsDto dto = reportsService.getReportsData(1L, "year");
        BudgetComparisonDto cmp = dto.getBudgetComparison().get(0);
        assertTrue(cmp.getBudgeted() >= 50.0); // monthsInPeriod * 50
        assertEquals("Healthcare", cmp.getCategory());
        assertTrue(cmp.getRemaining() <= cmp.getBudgeted());
    }

    @Test
    void testConvertCategoryName_AllCasesAndDefault() {
        try {
            var method = ReportsService.class.getDeclaredMethod("convertCategoryName", String.class);
            method.setAccessible(true);
            assertEquals("Food & Dining", method.invoke(reportsService, "FOOD_DINING"));
            assertEquals("Transportation", method.invoke(reportsService, "TRANSPORTATION"));
            assertEquals("Entertainment", method.invoke(reportsService, "ENTERTAINMENT"));
            assertEquals("Shopping", method.invoke(reportsService, "SHOPPING"));
            assertEquals("Bills & Utilities", method.invoke(reportsService, "BILLS_UTILITIES"));
            assertEquals("Healthcare", method.invoke(reportsService, "HEALTHCARE"));
            assertEquals("Travel", method.invoke(reportsService, "TRAVEL"));
            assertEquals("Education", method.invoke(reportsService, "EDUCATION"));
            assertEquals("Other", method.invoke(reportsService, "OTHER"));
            // default passthrough
            assertEquals("UNKNOWN", method.invoke(reportsService, "UNKNOWN"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testBudgetComparison_CategoryWithoutTransactions_ZeroSpentAndDefaultName() {
        // no OUT transactions in EDUCATION
        when(transactionRepository.findByUserIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        Budget b = new Budget();
        b.setCategory("UNKNOWN_CAT");
        b.setAmount(100.0);
        when(budgetRepository.findByUserId(anyLong())).thenReturn(List.of(b));
        when(savingLogRepository.findByUserIdOrderByTimestampDesc(anyLong())).thenReturn(Collections.emptyList());

        ReportsDto dto = reportsService.getReportsData(1L, "6months");
        BudgetComparisonDto cmp = dto.getBudgetComparison().get(0);
        // convertCategoryName will pass through unknown
        assertEquals("UNKNOWN_CAT", cmp.getCategory());
        assertTrue(cmp.getSpent() == 0.0);
    }

    @Test
    void testCategoryExpenses_PercentageZeroWhenNoTotal() {
        Transaction out = new Transaction();
        out.setType(TransactionType.OUT);
        out.setAmount(0.0);
        out.setDate(LocalDate.now());
        out.setExpenseCategory(ExpenseCategory.FOOD_DINING);
        when(transactionRepository.findByUserIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(List.of(out));
        when(budgetRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());
        when(savingLogRepository.findByUserIdOrderByTimestampDesc(anyLong())).thenReturn(Collections.emptyList());

        ReportsDto dto = reportsService.getReportsData(1L, "6months");
        // if total is zero, percentage string becomes 0.0
        if (!dto.getCategoryExpenses().isEmpty()) {
            assertEquals("0.0", dto.getCategoryExpenses().get(0).getPercentage());
        }
    }
}
