import { useMemo, useEffect, useState } from 'react'
import { Link } from 'react-router'
import { useTranslation } from 'react-i18next'
import {
  Plus,
  Target,
  TrendingUp,
  TrendingDown,
  AlertTriangle,
  Edit,
  MoreHorizontal,
  DollarSign,
  Calendar,
  PieChart
} from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Separator } from '@/components/ui/separator'

import { categories } from '@/data/staticData'
import { useBudgetStore } from '@/stores/budgetStore'
import { EditBudgetModal } from '@/components/EditBudgetModal'

export function Budgets() {
  const { t } = useTranslation()
  const { budgets, fetchBudgets, deleteBudget } = useBudgetStore()
  const [isDeleting, setIsDeleting] = useState(false)
  const [isEditModalOpen, setIsEditModalOpen] = useState(false)
  const [editingBudgetId, setEditingBudgetId] = useState<number | null>(null)
  useEffect(() => {
    fetchBudgets()
    const handleFocus = () => fetchBudgets()
    window.addEventListener('focus', handleFocus)
    return () => window.removeEventListener('focus', handleFocus)
  }, [fetchBudgets])

  const handleEditBudget = (budgetId: number) => {
    setEditingBudgetId(budgetId)
    setIsEditModalOpen(true)
  }

  const handleCloseEditModal = () => {
    setIsEditModalOpen(false)
    setEditingBudgetId(null)
  }

  const handleDeleteBudget = async (budgetId: number) => {
    if (!confirm(t('Are you sure you want to delete this budget?'))) return
    setIsDeleting(true)
    try {
      await deleteBudget(budgetId)
    } catch (error: any) {
      // Error handling is done in the store
    } finally {
      setIsDeleting(false)
    }
  }

  const budgetStats = useMemo(() => {
    if (!budgets.length) {
      return {
        totalBudgeted: 0,
        totalSpent: 0,
        remaining: 0,
        overBudgetCount: 0,
        underBudgetCount: 0,
        utilizationRate: 0
      }
    }
    const totalBudgeted = budgets.reduce((sum, b) => sum + b.amount, 0)
    const totalSpent = budgets.reduce((sum, b) => sum + b.spent, 0)
    const overBudgetCount = budgets.filter(b => b.spent > b.amount).length
    const underBudgetCount = budgets.filter(b => b.spent < b.amount * 0.8).length
    return {
      totalBudgeted,
      totalSpent,
      remaining: totalBudgeted - totalSpent,
      overBudgetCount,
      underBudgetCount,
      utilizationRate: totalBudgeted > 0 ? (totalSpent / totalBudgeted) * 100 : 0
    }
  }, [budgets])

  // Map enum category values to display names
  const categoryEnumToDisplayName = (enumValue: string): string => {
    const categoryMap: Record<string, string> = {
      'FOOD_DINING': 'Food & Dining',
      'TRANSPORTATION': 'Transportation',
      'ENTERTAINMENT': 'Entertainment',
      'SHOPPING': 'Shopping',
      'BILLS_UTILITIES': 'Bills & Utilities',
      'HEALTHCARE': 'Healthcare',
      'TRAVEL': 'Travel',
      'EDUCATION': 'Education',
      'OTHER': 'Other'
    }
    return categoryMap[enumValue] || enumValue
  }

  const getCategoryIcon = (categoryEnum: string) => {
    const displayName = categoryEnumToDisplayName(categoryEnum)
    return categories.find(c => c.name === displayName)?.icon || '📦'
  }
  
  const getCategoryColor = (categoryEnum: string) => {
    const displayName = categoryEnumToDisplayName(categoryEnum)
    return categories.find(c => c.name === displayName)?.color || '#95a5a6'
  }

  const getBudgetStatus = (spent: number, limit: number) => {
    const utilization = (spent / limit) * 100
    if (utilization >= 100) return { status: 'over', color: 'text-red-600 dark:text-red-400' }
    if (utilization >= 80) return { status: 'warning', color: 'text-amber-600 dark:text-amber-400' }
    return { status: 'healthy', color: 'text-green-600 dark:text-green-400' }
  }

  const getProgressColor = (spent: number, limit: number) => {
    const utilization = (spent / limit) * 100
    if (utilization >= 100) return 'bg-gradient-to-r from-red-500 to-red-600'
    if (utilization >= 80) return 'bg-gradient-to-r from-amber-500 to-orange-500'
    return 'bg-gradient-to-r from-green-500 to-emerald-500'
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white">
            {t('Budgets')}
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mt-1">
            {t('Track your spending limits')}
          </p>
        </div>
        <Link to="/budgets/add">
          <Button className="bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-600 hover:to-cyan-600 text-white rounded-2xl shadow-lg">
            <Plus className="w-4 h-4 mr-2" />
            {t('Add Budget')}
          </Button>
        </Link>
      </div>

      {/* Overview */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 lg:gap-6">
        {[
          { title: t('Total Budget'), icon: <Target />, color: 'from-blue-500 to-cyan-500', value: `$${budgetStats.totalBudgeted.toLocaleString()}` },
          { title: t('Total Spent'), icon: <DollarSign />, color: 'from-purple-500 to-pink-500', value: `$${budgetStats.totalSpent.toLocaleString()}` },
          { title: t('Remaining'), icon: <TrendingDown />, color: 'from-green-500 to-emerald-500', value: `$${budgetStats.remaining.toLocaleString()}` },
          { title: t('Utilization'), icon: <PieChart />, color: 'from-orange-500 to-red-500', value: `${budgetStats.utilizationRate.toFixed(1)}%` },
        ].map((stat, i) => (
          <Card key={i} className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
            <CardContent className="p-4 lg:p-6">
              <div className="flex items-center gap-3">
                <div className={`w-10 h-10 bg-gradient-to-r ${stat.color} rounded-xl flex items-center justify-center`}>
                  {stat.icon}
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{stat.title}</p>
                  <p className="text-xl font-bold text-gray-900 dark:text-white">{stat.value}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Budget Status */}
      <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
        <CardHeader>
          <CardTitle className="text-lg flex items-center gap-2">
            <Calendar className="w-5 h-5" />
            {t('Budget Status')}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-center">
            <div className="p-4 rounded-xl bg-red-50 dark:bg-red-900/20 border border-red-100 dark:border-red-800/30">
              <AlertTriangle className="w-6 h-6 text-red-500 mx-auto mb-2" />
              <p className="text-2xl font-bold text-red-600 dark:text-red-400">{budgetStats.overBudgetCount}</p>
              <p className="text-sm text-red-600 dark:text-red-400">{t('Over Budget')}</p>
            </div>
            <div className="p-4 rounded-xl bg-amber-50 dark:bg-amber-900/20 border border-amber-100 dark:border-amber-800/30">
              <TrendingUp className="w-6 h-6 text-amber-500 mx-auto mb-2" />
              <p className="text-2xl font-bold text-amber-600 dark:text-amber-400">
                {budgets.length - budgetStats.overBudgetCount - budgetStats.underBudgetCount}
              </p>
              <p className="text-sm text-amber-600 dark:text-amber-400">{t('Near Limit')}</p>
            </div>
            <div className="p-4 rounded-xl bg-green-50 dark:bg-green-900/20 border border-green-100 dark:border-green-800/30">
              <TrendingDown className="w-6 h-6 text-green-500 mx-auto mb-2" />
              <p className="text-2xl font-bold text-green-600 dark:text-green-400">{budgetStats.underBudgetCount}</p>
              <p className="text-sm text-green-600 dark:text-green-400">{t('Under Budget')}</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Category Budgets */}
      <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
        <CardHeader>
          <CardTitle className="text-lg">{t('Category Budgets')}</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-6">
            {budgets.map((budget, i) => {
              const utilization = budget.utilizationPercentage
              const status = getBudgetStatus(budget.spent, budget.amount)
              const barColor = getProgressColor(budget.spent, budget.amount)
              return (
                <div key={budget.id}>
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-3">
                      <div
                        className="w-10 h-10 rounded-xl flex items-center justify-center text-white"
                        style={{ backgroundColor: getCategoryColor(budget.category) }}
                      >
                        {getCategoryIcon(budget.category)}
                      </div>
                      <div>
                        <h3 className="font-medium text-gray-900 dark:text-white">{categoryEnumToDisplayName(budget.category)}</h3>
                        <p className="text-sm text-gray-500 dark:text-gray-400">
                          ${budget.spent.toLocaleString()} {t('of')} ${budget.amount.toLocaleString()}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center gap-3">
                      <Badge variant="secondary" className={`${status.color} font-medium`}>
                        {utilization.toFixed(0)}%
                      </Badge>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
                            <MoreHorizontal className="w-4 h-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" className="w-48">
                          <DropdownMenuItem 
                            className="flex items-center gap-2"
                            onClick={() => handleEditBudget(budget.id)}
                          >
                            <Edit className="w-4 h-4" />
                            {t('Edit Budget')}
                          </DropdownMenuItem>
                          <DropdownMenuItem
                            className="text-red-600 dark:text-red-400"
                            onClick={() => handleDeleteBudget(budget.id)}
                            disabled={isDeleting}
                          >
                            <AlertTriangle className="w-4 h-4" />
                            {t('Delete Budget')}
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </div>
                  </div>

                  {/* Progress bar */}
                  <div className="space-y-2">
                    <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                      <div
                        className={`h-2 rounded-full ${barColor}`}
                        style={{ width: `${Math.min(utilization, 100)}%` }}
                      />
                    </div>
                    <div className="flex justify-between text-xs text-gray-500 dark:text-gray-400">
                      <span>$0</span>
                      <span className="font-medium">
                        {t('Remaining')}: ${Math.max(budget.amount - budget.spent, 0).toLocaleString()}
                      </span>
                      <span>${budget.amount.toLocaleString()}</span>
                    </div>
                  </div>

                  {i < budgets.length - 1 && <Separator className="mt-6" />}
                </div>
              )
            })}
          </div>
        </CardContent>
      </Card>
      {/* Edit Budget Modal */}
      <EditBudgetModal
        isOpen={isEditModalOpen}
        onClose={handleCloseEditModal}
        budgetId={editingBudgetId}
      />
    </div>
  )
}
