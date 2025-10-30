import { api } from '@/lib/axios'
import { handleApiError, logError } from '@/utils/errorHandler'
import type { ApiResponse } from '@/types/auth'

export interface DepositRequest {
  amount: number
  description?: string
}

export interface Account {
  id: number
  accountNumber: string
  balance: number
  saved: number
}

// Deposit API service class
export class DepositService {
  // Process deposit
  static async processDeposit(depositData: DepositRequest): Promise<Account> {
    try {
      const response = await api.post<ApiResponse<Account>>('/api/deposit', depositData)
      
      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to process deposit')
      }
      
      return response.data.data
    } catch (error: any) {
      throw DepositService.handleError(error, 'Process deposit')
    }
  }

  // Unified error handling
  private static handleError(error: any, context: string): Error {
    logError(error, context)
    return handleApiError(error)
  }
}

// Export convenient API functions
export const depositApi = {
  processDeposit: DepositService.processDeposit
}
