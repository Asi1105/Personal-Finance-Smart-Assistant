import { api } from '@/lib/axios'
import { handleApiError, logError } from '@/utils/errorHandler'
import type { ApiResponse } from '@/types/auth'

export interface BudgetRequest {
  category: string
  amount: number
  period: string // e.g., "monthly", "weekly", "yearly"
}

export interface Budget {
  id: number
  category: string
  amount: number
  period: string
  spent: number
  remaining: number
  utilizationPercentage: number
}

export class BudgetService {
  static async addBudget(budgetData: BudgetRequest): Promise<Budget> {
    try {
      const response = await api.post<ApiResponse<Budget>>('/api/budgets', budgetData)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to add budget')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Add budget')
      throw handleApiError(error)
    }
  }

  static async getBudgets(): Promise<Budget[]> {
    try {
      const response = await api.get<ApiResponse<Budget[]>>('/api/budgets')

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to get budgets')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Get budgets')
      throw handleApiError(error)
    }
  }

  static async getBudget(budgetId: number): Promise<Budget> {
    try {
      const response = await api.get<ApiResponse<Budget>>(`/api/budgets/${budgetId}`)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to get budget')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Get budget')
      throw handleApiError(error)
    }
  }

  static async updateBudget(budgetId: number, budgetData: BudgetRequest): Promise<Budget> {
    try {
      const response = await api.put<ApiResponse<Budget>>(`/api/budgets/${budgetId}`, budgetData)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to update budget')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Update budget')
      throw handleApiError(error)
    }
  }

  static async deleteBudget(budgetId: number): Promise<void> {
    try {
      const response = await api.delete<ApiResponse<void>>(`/api/budgets/${budgetId}`)

      if (!response.data.success) {
        throw new Error(response.data.message || 'Failed to delete budget')
      }
    } catch (error: any) {
      logError(error, 'Delete budget')
      throw handleApiError(error)
    }
  }
}

export const budgetApi = {
  addBudget: BudgetService.addBudget,
  getBudgets: BudgetService.getBudgets,
  getBudget: BudgetService.getBudget,
  updateBudget: BudgetService.updateBudget,
  deleteBudget: BudgetService.deleteBudget,
}