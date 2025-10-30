package com.sydney.uni.backend.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExpenseCategoryTest {

    @Test
    void testEnumValues() {
        // 测试 .values() 方法
        ExpenseCategory[] categories = ExpenseCategory.values();
        assertTrue(categories.length > 0);
        assertEquals(ExpenseCategory.FOOD_DINING, categories[0]);
    }

    @Test
    void testEnumValueOf() {
        // 测试 .valueOf() 方法
        ExpenseCategory food = ExpenseCategory.valueOf("FOOD_DINING");
        assertEquals(ExpenseCategory.FOOD_DINING, food);

        ExpenseCategory travel = ExpenseCategory.valueOf("TRAVEL");
        assertEquals(ExpenseCategory.TRAVEL, travel);
    }
}