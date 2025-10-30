import { create } from 'zustand'
import { budgetApi, type Budget } from '@/services/budgetService'
import toast from 'react-hot-toast'

interface BudgetState {
  budgets: Budget[]
  isLoading: boolean
  error: string | null

  fetchBudgets: () => Promise<void>
  addBudget: (budgetData: { category: string; amount: number; period: string }) => Promise<void>
  updateBudget: (budgetId: number, budgetData: { category: string; amount: number; period: string }) => Promise<void>
  deleteBudget: (budgetId: number) => Promise<void>
  clearError: () => void
}

export const useBudgetStore = create<BudgetState>((set) => ({
  budgets: [],
  isLoading: false,
  error: null,

  fetchBudgets: async () => {
    set({ isLoading: true, error: null })
    try {
      const budgets = await budgetApi.getBudgets()
      set({ budgets, isLoading: false })
    } catch (error: any) {
      set({ error: error.message, isLoading: false })
      toast.error(error.message || 'Failed to load budgets')
    }
  },

  addBudget: async (budgetData) => {
    set({ isLoading: true, error: null })
    try {
      const newBudget = await budgetApi.addBudget(budgetData)
      set((state) => ({
        budgets: [...state.budgets, newBudget],
        isLoading: false
      }))
      toast.success('Budget added successfully!')
    } catch (error: any) {
      set({ error: error.message, isLoading: false })
      toast.error(error.message || 'Failed to add budget')
      throw error
    }
  },

  updateBudget: async (budgetId, budgetData) => {
    set({ isLoading: true, error: null })
    try {
      const updatedBudget = await budgetApi.updateBudget(budgetId, budgetData)
      set((state) => ({
        budgets: state.budgets.map(budget => 
          budget.id === budgetId ? updatedBudget : budget
        ),
        isLoading: false
      }))
      toast.success('Budget updated successfully!')
    } catch (error: any) {
      set({ error: error.message, isLoading: false })
      toast.error(error.message || 'Failed to update budget')
      throw error
    }
  },

  deleteBudget: async (budgetId) => {
    set({ isLoading: true, error: null })
    try {
      await budgetApi.deleteBudget(budgetId)
      set((state) => ({
        budgets: state.budgets.filter(budget => budget.id !== budgetId),
        isLoading: false
      }))
      toast.success('Budget deleted successfully!')
    } catch (error: any) {
      set({ error: error.message, isLoading: false })
      toast.error(error.message || 'Failed to delete budget')
      throw error
    }
  },

  clearError: () => set({ error: null }),
}))
