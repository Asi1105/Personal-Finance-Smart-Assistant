import { api } from '@/lib/axios'
import { handleApiError, logError } from '@/utils/errorHandler'
import type { ApiResponse } from '@/types/auth'

export interface SaveMoneyRequest {
  amount: number
  description?: string
}

export interface Account {
  id: number
  bank: string
  accountNumber: string
  balance: number
  saved: number
}

// SaveMoney API service class
export class SaveMoneyService {
  // Save money to savings
  static async saveMoney(saveMoneyData: SaveMoneyRequest): Promise<Account> {
    try {
      const response = await api.post<ApiResponse<Account>>('/api/save-money', saveMoneyData)
      
      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to save money')
      }
      
      return response.data.data
    } catch (error: any) {
      throw SaveMoneyService.handleError(error, 'Save money')
    }
  }

  // Unified error handling
  private static handleError(error: any, context: string): Error {
    logError(error, context)
    return handleApiError(error)
  }
}

// Export convenient API functions
export const saveMoneyApi = {
  saveMoney: SaveMoneyService.saveMoney
}
