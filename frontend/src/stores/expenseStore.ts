import { create } from 'zustand'
import { expenseApi } from '@/services/expenseService'
import type { Transaction } from '@/types'
import toast from 'react-hot-toast'

interface ExpenseState {
  expenses: Transaction[]
  isLoading: boolean
  error: string | null

  fetchExpenses: () => Promise<void>
  addExpense: (expenseData: { description: string; category: string; amount: number; date: string; notes?: string }) => Promise<void>
  clearError: () => void
}

export const useExpenseStore = create<ExpenseState>((set) => ({
  expenses: [],
  isLoading: false,
  error: null,

  fetchExpenses: async () => {
    set({ isLoading: true, error: null })
    try {
      const expenses = await expenseApi.getExpenses()
      set({ expenses, isLoading: false })
    } catch (error: any) {
      set({ error: error.message, isLoading: false })
      toast.error(error.message || 'Failed to load expenses')
    }
  },

  addExpense: async (expenseData) => {
    set({ isLoading: true, error: null })
    try {
      const newExpense = await expenseApi.addExpense(expenseData)
      set((state) => ({
        expenses: [newExpense, ...state.expenses],
        isLoading: false
      }))
      toast.success('Expense added successfully!')
    } catch (error: any) {
      set({ error: error.message, isLoading: false })
      toast.error(error.message || 'Failed to add expense')
      throw error
    }
  },

  clearError: () => set({ error: null }),
}))
