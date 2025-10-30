import { api } from '@/lib/axios'
import { handleApiError, logError } from '@/utils/errorHandler'
import type { ApiResponse } from '@/types/auth'

export interface SaveGoalRequest {
  targetAmount: number
  description?: string
}

export interface SaveGoal {
  id: number
  targetAmount: number
  description?: string
  createdAt: string
  updatedAt: string
}

export class SaveGoalService {
  static async setSaveGoal(saveGoalData: SaveGoalRequest): Promise<SaveGoal> {
    try {
      const response = await api.post<ApiResponse<SaveGoal>>('/api/save-goals', saveGoalData)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to set save goal')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Set save goal')
      throw handleApiError(error)
    }
  }

  static async getSaveGoal(): Promise<SaveGoal | null> {
    try {
      const response = await api.get<ApiResponse<SaveGoal>>('/api/save-goals')

      if (!response.data.success) {
        return null
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Get save goal')
      throw handleApiError(error)
    }
  }

  static async updateSaveGoal(saveGoalData: SaveGoalRequest): Promise<SaveGoal> {
    try {
      const response = await api.put<ApiResponse<SaveGoal>>('/api/save-goals', saveGoalData)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to update save goal')
      }

      return response.data.data
    } catch (error: any) {
      logError(error, 'Update save goal')
      throw handleApiError(error)
    }
  }

  static async deleteSaveGoal(): Promise<void> {
    try {
      await api.delete('/api/save-goals')
    } catch (error: any) {
      logError(error, 'Delete save goal')
      throw handleApiError(error)
    }
  }
}

export const saveGoalApi = {
  setSaveGoal: SaveGoalService.setSaveGoal,
  getSaveGoal: SaveGoalService.getSaveGoal,
  updateSaveGoal: SaveGoalService.updateSaveGoal,
  deleteSaveGoal: SaveGoalService.deleteSaveGoal,
}
