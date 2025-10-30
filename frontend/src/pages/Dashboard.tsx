import { useEffect } from 'react'
import { Link } from 'react-router'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { StatsLoading, Skeleton } from '@/components/ui/loading'
import { 
  TrendingUp, 
  TrendingDown, 
  DollarSign, 
  CreditCard,
  Target,
  ArrowUpRight,
  ArrowDownRight,
  Plus,
  Eye,
  Sparkles,
  Calendar
} from 'lucide-react'
import { useDashboardStore } from '@/stores/dashboardStore'
import { useAuthStore } from '@/stores/authStore'
import { useTranslation } from 'react-i18next'
import { format } from 'date-fns'

export function Dashboard() {
  const { t } = useTranslation()
  const { user } = useAuthStore()
  const {
    stats,
    recentTransactions,
    isLoading,
    fetchDashboardStats,
    fetchRecentTransactions
  } = useDashboardStore()

  // Load dashboard data on component mount
  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        await Promise.all([
          fetchDashboardStats(),
          fetchRecentTransactions(10)
        ])
      } catch (error) {
        console.error('Failed to load dashboard data:', error)
      }
    }

    loadDashboardData()
  }, [fetchDashboardStats, fetchRecentTransactions])

  return (
    <div className="space-y-6 lg:space-y-8">
      {/* Welcome Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold bg-gradient-to-r from-gray-900 dark:from-white via-violet-600 to-purple-600 bg-clip-text text-transparent mb-2">
            {t('welcome_back')}, {user?.name || 'User'}! ✨
          </h1>
          <p className="text-gray-600 dark:text-gray-300 text-sm sm:text-base lg:text-lg">
            {t('dashboardSubtitle')}
          </p>
        </div>
        <div className="flex gap-3">
          <Link to="/expenses/add">
            <Button className="bg-gradient-to-r from-violet-500 to-purple-500 hover:from-violet-600 hover:to-purple-600 text-white px-4 sm:px-6 py-2.5 sm:py-3 text-sm sm:text-base rounded-2xl shadow-lg shadow-violet-500/25 hover:shadow-violet-500/40 transition-all duration-300 transform hover:scale-105">
              <Plus className="w-4 h-4 mr-2" />
              <span className="hidden sm:inline">{t('addExpense')}</span>
              <span className="sm:hidden">{t('addExpense')}</span>
            </Button>
          </Link>
        </div>
      </div>

      {/* Stats Cards */}
      {isLoading ? (
        <StatsLoading count={4} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 lg:gap-6" />
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 lg:gap-6">
        {/* Total Balance */}
        <Card className="relative overflow-hidden bg-gradient-to-br from-violet-500 to-purple-600 border-0 text-white shadow-2xl shadow-violet-500/25">
          <div className="absolute top-0 right-0 -m-4 w-24 h-24 bg-white/10 rounded-full blur-2xl"></div>
          <CardHeader className="relative z-10 pb-2">
            <div className="flex items-center justify-between">
              <CardTitle className="text-sm font-medium text-violet-100">{t('totalBalance')}</CardTitle>
              <DollarSign className="w-5 h-5 text-violet-200" />
            </div>
          </CardHeader>
          <CardContent className="relative z-10">
            <div className="text-3xl font-bold mb-1">${stats?.totalBalance?.toFixed(2) || '0.00'}</div>
            <div className="flex items-center text-sm text-violet-200">
              <TrendingUp className="w-4 h-4 mr-1" />
              {(() => {
                const change = stats?.monthlySpendingChange;
                const lastMonth = stats?.lastMonthSpending;
                if (lastMonth === 0 && (stats?.monthlySpending || 0) > 0) {
                  return t('newSpending');
                } else if (lastMonth === 0 && (stats?.monthlySpending || 0) === 0) {
                  return t('noSpending');
                } else if (change && change > 0) {
                  return `+${change.toFixed(1)}% ${t('fromLastMonth')}`;
                } else if (change && change < 0) {
                  return `${change.toFixed(1)}% ${t('fromLastMonth')}`;
                } else {
                  return t('sameAsLastMonth');
                }
              })()}
            </div>
          </CardContent>
        </Card>

        {/* Monthly Spending */}
        <Card className="relative overflow-hidden bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50 shadow-lg hover:shadow-xl transition-all duration-300 group">
          <div className="absolute top-0 right-0 -m-4 w-24 h-24 bg-gradient-to-br from-emerald-400/20 to-cyan-400/20 rounded-full blur-2xl group-hover:blur-xl transition-all duration-300"></div>
          <CardHeader className="relative z-10 pb-2">
            <div className="flex items-center justify-between">
              <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">{t('expensesThisMonth')}</CardTitle>
              <CreditCard className="w-5 h-5 text-emerald-500" />
            </div>
          </CardHeader>
          <CardContent className="relative z-10">
            <div className="text-2xl lg:text-3xl font-bold text-gray-900 dark:text-white mb-1">${stats?.monthlySpending?.toFixed(2) || '0.00'}</div>
            <div className="flex items-center text-sm text-red-600 dark:text-red-400">
              <TrendingDown className="w-4 h-4 mr-1" />
              {(() => {
                const change = stats?.monthlySpendingChange;
                const lastMonth = stats?.lastMonthSpending;
                const thisMonth = stats?.monthlySpending;

                if (lastMonth === 0 && (thisMonth || 0) > 0) {
                  return t('newExpenses');
                } else if (change === null || change === undefined) {
                  return `0% ${t('vsLastMonth')}`;
                } else {
                  const sign = change > 0 ? '+' : '';
                  return `${sign}${change.toFixed(1)}% ${t('vsLastMonth')}`;
                }
              })()}
            </div>
          </CardContent>
        </Card>

        {/* Budget Status */}
        <Card className="relative overflow-hidden bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50 shadow-lg hover:shadow-xl transition-all duration-300 group">
          <div className="absolute top-0 right-0 -m-4 w-24 h-24 bg-gradient-to-br from-amber-400/20 to-orange-400/20 rounded-full blur-2xl group-hover:blur-xl transition-all duration-300"></div>
          <CardHeader className="relative z-10 pb-2">
            <div className="flex items-center justify-between">
              <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">{t('budgetLeft')}</CardTitle>
              <Target className="w-5 h-5 text-amber-500" />
            </div>
          </CardHeader>
          <CardContent className="relative z-10">
            <div className="text-2xl lg:text-3xl font-bold text-gray-900 dark:text-white mb-1">${stats?.budgetLeft?.toFixed(2) || '0.00'}</div>
            <div className="flex items-center text-sm text-amber-600 dark:text-amber-400">
              <Eye className="w-4 h-4 mr-1" />
              {(() => {
                const usedPercentage = stats?.budgetUsedPercentage;
                if (usedPercentage === null || usedPercentage === undefined) {
                  return t('noBudgetSet');
                }
                return `${usedPercentage.toFixed(0)}% ${t('ofBudgetUsed')}`;
              })()}
            </div>
          </CardContent>
        </Card>

        {/* Savings Goal */}
        <Card className="relative overflow-hidden bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50 shadow-lg hover:shadow-xl transition-all duration-300 group">
          <div className="absolute top-0 right-0 -m-4 w-24 h-24 bg-gradient-to-br from-pink-400/20 to-rose-400/20 rounded-full blur-2xl group-hover:blur-xl transition-all duration-300"></div>
          <CardHeader className="relative z-10 pb-2">
            <div className="flex items-center justify-between">
              <CardTitle className="text-sm font-medium text-gray-600 dark:text-gray-400">{t('savingsGoal')}</CardTitle>
              <Sparkles className="w-5 h-5 text-pink-500" />
            </div>
          </CardHeader>
          <CardContent className="relative z-10">
            <div className="text-2xl lg:text-3xl font-bold text-gray-900 dark:text-white mb-1">
              ${stats?.saved?.toFixed(2) || '0.00'}
            </div>
            <div className="space-y-1">
              {stats?.hasSavingsGoal ? (
                <>
                  <div className="flex items-center text-sm text-pink-600 dark:text-pink-400">
                    <TrendingUp className="w-4 h-4 mr-1" />
                    {stats?.savingsProgress ? `${(100 - stats.savingsProgress).toFixed(0)}%` : '100%'} {t('toGoal')}
                  </div>
                  <div className="text-xs text-gray-500 dark:text-gray-400">
                    {t('goal')}: ${stats?.savingsGoal?.toFixed(0) || '0'}
                  </div>
                </>
              ) : (
                <div className="text-sm text-gray-500 dark:text-gray-400">
                  {t('noSavingsGoal')}
                </div>
              )}
            </div>
          </CardContent>
        </Card>
        </div>
      )}

      {/* Recent Transactions */}
      <div className="grid grid-cols-1 gap-6 lg:gap-8">
        <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50 shadow-lg">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-gray-900 dark:text-white">
              <div className="w-2 h-2 bg-violet-500 rounded-full animate-pulse"></div>
              {t('recentTransactions')}
            </CardTitle>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="space-y-4">
                {Array.from({ length: 4 }, (_, index) => (
                  <div key={`skeleton-${index}`} className="flex items-center justify-between p-3 rounded-2xl bg-gray-50/50 dark:bg-gray-700/50">
                    <div className="flex items-center gap-3">
                      <Skeleton className="w-10 h-10 rounded-xl" />
                      <div className="space-y-2">
                        <Skeleton className="h-4 w-32" />
                        <Skeleton className="h-3 w-20" />
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <Skeleton className="h-4 w-16" />
                      <Skeleton className="w-4 h-4 rounded-full" />
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="space-y-4">
              {recentTransactions.length > 0 ? recentTransactions.map((transaction) => (
                <div key={transaction.id} className="grid grid-cols-12 items-center p-3 rounded-2xl bg-gray-50/50 dark:bg-gray-700/50 hover:bg-white dark:hover:bg-gray-600/50 transition-colors duration-200 gap-4">
                  {/* Left section - Transaction info */}
                  <div className="col-span-5 flex items-center gap-3">
                    <div className="w-10 h-10 bg-gradient-to-r from-violet-100 to-purple-100 dark:from-violet-800/30 dark:to-purple-800/30 rounded-xl flex items-center justify-center text-lg">
                      {(transaction as any).icon || '💰'}
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="font-semibold text-gray-900 dark:text-white text-sm sm:text-base truncate">{transaction.detail}</p>
                      <p className="text-xs sm:text-sm text-gray-500 dark:text-gray-400 truncate">
                        {transaction.type === 'IN' && transaction.note ? transaction.note : (transaction as any).categoryDisplayName || transaction.expenseCategory || 'Other'}
                      </p>
                    </div>
                  </div>
                  
                  {/* Center section - Date */}
                  <div className="col-span-3 flex items-center justify-center gap-2 text-sm text-gray-600 dark:text-gray-300">
                    <Calendar className="w-4 h-4" />
                    <span className="font-medium">
                      {format(new Date(transaction.date), 'MMM dd')}
                    </span>
                    <span className="text-xs text-gray-500 dark:text-gray-400">
                      {format(new Date(transaction.date), 'EEEE')}
                    </span>
                  </div>
                  
                  {/* Right section - Amount */}
                  <div className="col-span-4 flex items-center justify-end gap-2">
                    <span className={`font-bold text-sm sm:text-base ${transaction.type === 'IN' ? 'text-emerald-600 dark:text-emerald-400' : 'text-gray-900 dark:text-white'}`}>
                      {transaction.type === 'IN' ? '+' : '-'}${transaction.amount.toFixed(2)}
                    </span>
                    {transaction.type === 'IN' ?
                      <ArrowUpRight className="w-4 h-4 text-emerald-500" /> :
                      <ArrowDownRight className="w-4 h-4 text-red-500" />
                    }
                  </div>
                </div>
              )) : (
                <div className="text-center py-8 text-gray-500 dark:text-gray-400">
                  <p>{t('noRecentTransactions')}</p>
                </div>
              )}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
