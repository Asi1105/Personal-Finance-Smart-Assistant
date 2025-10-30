import { create } from 'zustand'
import { dashboardApi, type DashboardStats } from '@/services/dashboardService'
import type { Transaction } from '@/types'
import toast from 'react-hot-toast'

interface DashboardState {
  stats: DashboardStats | null
  recentTransactions: Transaction[]
  isLoading: boolean
  error: string | null
  
  // Actions
  fetchDashboardStats: () => Promise<void>
  fetchRecentTransactions: (limit?: number) => Promise<void>
  clearError: () => void
  setLoading: (loading: boolean) => void
}

export const useDashboardStore = create<DashboardState>((set) => ({
  stats: null,
  recentTransactions: [],
  isLoading: false,
  error: null,

  fetchDashboardStats: async () => {
    set({ isLoading: true, error: null })
    
    try {
      const stats = await dashboardApi.getDashboardStats()
      set({ stats, isLoading: false, error: null })
    } catch (error: any) {
      set({ stats: null, isLoading: false, error: error.message })
      toast.error(error.message || 'Failed to load dashboard stats')
      throw error
    }
  },

  fetchRecentTransactions: async (limit = 10) => {
    set({ isLoading: true, error: null })
    
    try {
      const transactions = await dashboardApi.getRecentTransactions(limit)
      set({ recentTransactions: transactions, isLoading: false, error: null })
    } catch (error: any) {
      set({ recentTransactions: [], isLoading: false, error: error.message })
      toast.error(error.message || 'Failed to load recent transactions')
      throw error
    }
  },

  clearError: () => set({ error: null }),
  
  setLoading: (loading: boolean) => set({ isLoading: loading })
}))
