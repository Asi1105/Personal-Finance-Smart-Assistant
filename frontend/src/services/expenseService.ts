import { api } from '@/lib/axios'
import { handleApiError, logError } from '@/utils/errorHandler'
import type { ApiResponse } from '@/types/auth'
import type { Transaction } from '@/types'

export interface ExpenseRequest {
  description: string
  category: string
  amount: number
  date: string
  notes?: string
}

export class ExpenseService {
  static async addExpense(expenseData: ExpenseRequest): Promise<Transaction> {
    try {
      const response = await api.post<ApiResponse<Transaction>>('/api/expenses', expenseData)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to add expense')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Add expense')
      throw handleApiError(error)
    }
  }

  static async getExpenses(): Promise<Transaction[]> {
    try {
      const response = await api.get<ApiResponse<Transaction[]>>('/api/expenses')

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to get expenses')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Get expenses')
      throw handleApiError(error)
    }
  }

  static async getExpense(id: string): Promise<Transaction> {
    try {
      const response = await api.get<ApiResponse<Transaction>>(`/api/expenses/${id}`)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to get expense')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Get expense')
      throw handleApiError(error)
    }
  }

  static async updateExpense(id: string, expenseData: ExpenseRequest): Promise<Transaction> {
    try {
      const response = await api.put<ApiResponse<Transaction>>(`/api/expenses/${id}`, expenseData)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to update expense')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Update expense')
      throw handleApiError(error)
    }
  }

  static async deleteExpense(id: string): Promise<void> {
    try {
      const response = await api.delete<ApiResponse<void>>(`/api/expenses/${id}`)

      if (!response.data.success) {
        throw new Error(response.data.message || 'Failed to delete expense')
      }
    } catch (error: any) {
      logError(error, 'Delete expense')
      throw handleApiError(error)
    }
  }
}

export const expenseApi = {
  addExpense: ExpenseService.addExpense,
  getExpenses: ExpenseService.getExpenses,
  getExpense: ExpenseService.getExpense,
  updateExpense: ExpenseService.updateExpense,
  deleteExpense: ExpenseService.deleteExpense,
}