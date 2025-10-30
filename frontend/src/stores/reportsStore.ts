import { create } from 'zustand'
import { reportsApi, type ReportsData } from '@/services/reportsService'

interface ReportsState {
  data: ReportsData | null
  isLoading: boolean
  error: string | null
  selectedPeriod: string
}

interface ReportsActions {
  fetchReportsData: (period?: string) => Promise<void>
  setSelectedPeriod: (period: string) => void
  clearError: () => void
}

type ReportsStore = ReportsState & ReportsActions

export const useReportsStore = create<ReportsStore>((set, get) => ({
  // State
  data: null,
  isLoading: false,
  error: null,
  selectedPeriod: '6months',

  // Actions
  fetchReportsData: async (period?: string) => {
    const currentPeriod = period || get().selectedPeriod
    set({ isLoading: true, error: null })

    try {
      const data = await reportsApi.getReportsData(currentPeriod)
      set({ data, isLoading: false })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to fetch reports data'
      set({ error: errorMessage, isLoading: false })
    }
  },

  setSelectedPeriod: (period: string) => {
    set({ selectedPeriod: period })
  },

  clearError: () => {
    set({ error: null })
  }
}))
