import { api } from '@/lib/axios'
import { handleApiError, logError } from '@/utils/errorHandler'
import type { ApiResponse } from '@/types/auth'

export interface SaveMoneyRequest {
  amount: number
  description?: string
}

export interface UnsaveMoneyRequest {
  amount: number
  description?: string
}

export interface SavingLog {
  id: number
  action: 'SAVE' | 'UNSAVE'
  amount: number
  description: string
  timestamp: string
  actionDisplayName: string
  icon: string
}

export interface Account {
  id: number
  accountNumber: string
  balance: number
  saved: number
}

export class SavingService {
  static async saveMoney(saveData: SaveMoneyRequest): Promise<Account> {
    try {
      const response = await api.post<ApiResponse<Account>>('/api/saving/save', saveData)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to save money')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Save money')
      throw handleApiError(error)
    }
  }

  static async unsaveMoney(unsaveData: UnsaveMoneyRequest): Promise<Account> {
    try {
      const response = await api.post<ApiResponse<Account>>('/api/saving/unsave', unsaveData)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to unsave money')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Unsave money')
      throw handleApiError(error)
    }
  }

  static async getSavingLogs(): Promise<SavingLog[]> {
    try {
      const response = await api.get<ApiResponse<SavingLog[]>>('/api/saving/logs')

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to get saving logs')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Get saving logs')
      throw handleApiError(error)
    }
  }
}

export const savingApi = {
  saveMoney: SavingService.saveMoney,
  unsaveMoney: SavingService.unsaveMoney,
  getSavingLogs: SavingService.getSavingLogs,
}
