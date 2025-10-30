import { create } from 'zustand'

interface Expense {
  id: string
  amount: number
  category: string
  description: string
  date: string
  userId: string
}

interface Budget {
  id: string
  category: string
  limit: number
  userId: string
}

interface FinanceState {
  expenses: Expense[]
  budgets: Budget[]
  totalSpent: number
  addExpense: (expense: Expense) => void
  updateExpense: (id: string, expense: Partial<Expense>) => void
  deleteExpense: (id: string) => void
  setBudget: (budget: Budget) => void
  setExpenses: (expenses: Expense[]) => void
  setBudgets: (budgets: Budget[]) => void
}

export const useFinanceStore = create<FinanceState>((set) => ({
  expenses: [],
  budgets: [],
  totalSpent: 0,
  
  addExpense: (expense) =>
    set((state) => ({
      expenses: [...state.expenses, expense],
      totalSpent: state.totalSpent + expense.amount,
    })),
    
  updateExpense: (id, updatedExpense) =>
    set((state) => ({
      expenses: state.expenses.map((expense) =>
        expense.id === id ? { ...expense, ...updatedExpense } : expense
      ),
    })),
    
  deleteExpense: (id) =>
    set((state) => {
      const expense = state.expenses.find((e) => e.id === id)
      return {
        expenses: state.expenses.filter((e) => e.id !== id),
        totalSpent: state.totalSpent - (expense?.amount || 0),
      }
    }),
    
  setBudget: (budget) =>
    set((state) => ({
      budgets: state.budgets.some((b) => b.id === budget.id)
        ? state.budgets.map((b) => (b.id === budget.id ? budget : b))
        : [...state.budgets, budget],
    })),
    
  setExpenses: (expenses) =>
    set({
      expenses,
      totalSpent: expenses.reduce((sum, expense) => sum + expense.amount, 0),
    }),
    
  setBudgets: (budgets) => set({ budgets }),
}))