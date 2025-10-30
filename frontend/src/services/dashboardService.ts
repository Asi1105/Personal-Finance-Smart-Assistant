import { api } from '@/lib/axios'
import { handleApiError, logError } from '@/utils/errorHandler'
import type { ApiResponse } from '@/types/auth'
import type { Transaction } from '@/types'

export interface DashboardStats {
  totalBalance: number
  saved: number
  monthlySpending: number
  budgetLeft: number
  savingsGoal: number
  savingsProgress: number
  budgetUsedPercentage: number
  monthlySpendingChange: number
  lastMonthSpending: number
  hasSavingsGoal: boolean
}

export interface DashboardTransaction extends Transaction {
  categoryDisplayName: string
  icon: string
}

// Dashboard API service class
export class DashboardService {
  // Get dashboard statistics
  static async getDashboardStats(): Promise<DashboardStats> {
    try {
      const response = await api.get<ApiResponse<DashboardStats>>('/api/dashboard/stats')
      
      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to get dashboard stats')
      }
      
      return response.data.data
    } catch (error: any) {
      throw DashboardService.handleError(error, 'Get dashboard stats')
    }
  }

  // Get recent transactions
  static async getRecentTransactions(limit: number = 10): Promise<DashboardTransaction[]> {
    try {
      const response = await api.get<ApiResponse<DashboardTransaction[]>>(`/api/dashboard/transactions?limit=${limit}`)
      
      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to get recent transactions')
      }
      
      return response.data.data
    } catch (error: any) {
      throw DashboardService.handleError(error, 'Get recent transactions')
    }
  }

  // Unified error handling
  private static handleError(error: any, context: string): Error {
    logError(error, context)
    return handleApiError(error)
  }
}

// Export convenient API functions
export const dashboardApi = {
  getDashboardStats: DashboardService.getDashboardStats,
  getRecentTransactions: DashboardService.getRecentTransactions
}
