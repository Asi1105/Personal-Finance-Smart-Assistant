import { api } from '@/lib/axios'
import { handleApiError } from '@/utils/errorHandler'

export interface MonthlyData {
  month: string
  income: number
  expenses: number
  savings: number
}

export interface CategoryExpense {
  category: string
  amount: number
  color: string
  percentage: string
}

export interface BudgetComparison {
  category: string
  budgeted: number
  spent: number
  remaining: number
}

export interface ReportsMetrics {
  totalIncome: number
  totalExpenses: number
  totalSavings: number
  avgMonthlyExpenses: number
  savingsRate: number
}

export interface ReportsData {
  monthlyData: MonthlyData[]
  categoryExpenses: CategoryExpense[]
  budgetComparison: BudgetComparison[]
  metrics: ReportsMetrics
}

class ReportsService {
  /**
   * Get reports data for the specified period
   */
  async getReportsData(period: string = '6months'): Promise<ReportsData> {
    try {
      const response = await api.get(`/api/reports?period=${period}`)
      return response.data
    } catch (error) {
      throw handleApiError(error)
    }
  }
}

export const reportsApi = new ReportsService()
