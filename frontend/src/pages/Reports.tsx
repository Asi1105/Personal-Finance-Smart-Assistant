import { useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts'
import {
  TrendingUp,
  TrendingDown,
  PieChart as PieChartIcon,
  BarChart3,
  Calendar,
  DollarSign,
  Target
} from 'lucide-react'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

import { useReportsStore } from '@/stores/reportsStore'
import { categories } from '@/data/staticData'
import toast from 'react-hot-toast'

// Tooltip components
const CustomTooltip = ({ active, payload, label }: any) => {
  if (active && payload?.length) {
    return (
      <div className="bg-white dark:bg-gray-800 p-3 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700">
        <p className="font-medium text-gray-900 dark:text-white">{label}</p>
        {payload.map((entry: any) => (
          <p key={`tooltip-${entry.name}`} style={{ color: entry.color }} className="text-sm">
            {entry.name}: ${entry.value?.toLocaleString()}
          </p>
        ))}
      </div>
    )
  }
  return null
}

const PieTooltip = ({ active, payload }: any) => {
  if (active && payload?.length) {
    const data = payload[0].payload
    return (
      <div className="bg-white dark:bg-gray-800 p-3 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700">
        <p className="font-medium text-gray-900 dark:text-white">{data.category}</p>
        <p className="text-sm text-gray-600 dark:text-gray-300">
          ${data.amount.toLocaleString()} ({data.percentage}%)
        </p>
      </div>
    )
  }
  return null
}

export function Reports() {
  const { t } = useTranslation()
  const {
    data,
    isLoading,
    error,
    selectedPeriod,
    fetchReportsData,
    setSelectedPeriod
  } = useReportsStore()

  useEffect(() => {
    fetchReportsData()
  }, [])

  useEffect(() => {
    if (error) toast.error(error)
  }, [error])

  const handlePeriodChange = (period: string) => {
    setSelectedPeriod(period)
    fetchReportsData(period)
  }

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-violet-500 mx-auto mb-4"></div>
            <p className="text-gray-600 dark:text-gray-300">{t('Loading reports...')}</p>
          </div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <p className="text-red-600 dark:text-red-400 mb-4">{t('Failed to load reports data')}</p>
            <button
              onClick={() => fetchReportsData()}
              className="px-4 py-2 bg-violet-500 text-white rounded-lg hover:bg-violet-600"
            >
              {t('Retry')}
            </button>
          </div>
        </div>
      </div>
    )
  }

  if (!data) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <p className="text-gray-600 dark:text-gray-300">{t('No data available')}</p>
          </div>
        </div>
      </div>
    )
  }

  const monthlyData = data.monthlyData || []
  const budgetComparisonData = data.budgetComparison || []
  const budgetComparison = categories.map(category => {
    const existingData = budgetComparisonData.find(item => item.category === category.name)
    return existingData || {
      category: category.name,
      budgeted: 0,
      spent: 0,
      remaining: 0
    }
  })

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white">
            {t('reportsTitle')}
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mt-1">
            {t('reportsSubtitle')}
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Select value={selectedPeriod} onValueChange={handlePeriodChange}>
            <SelectTrigger className="w-40 bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="6months">{t('Last 6 Months')}</SelectItem>
              <SelectItem value="year">{t('This Year')}</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4 lg:gap-6">
        {/* Total Income */}
        <Card className="bg-white/80 dark:bg-gray-800/80 border border-gray-200/50 dark:border-gray-700/50">
          <CardContent className="p-4 lg:p-6">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-r from-green-500 to-emerald-500 rounded-xl flex items-center justify-center">
                <TrendingUp className="w-5 h-5 text-white" />
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('totalIncome')}</p>
                <p className="text-xl font-bold text-gray-900 dark:text-white">
                  ${data.metrics.totalIncome.toLocaleString()}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Total Expenses */}
        <Card className="bg-white/80 dark:bg-gray-800/80 border border-gray-200/50 dark:border-gray-700/50">
          <CardContent className="p-4 lg:p-6">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-r from-red-500 to-pink-500 rounded-xl flex items-center justify-center">
                <TrendingDown className="w-5 h-5 text-white" />
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('totalExpenses')}</p>
                <p className="text-xl font-bold text-gray-900 dark:text-white">
                  ${data.metrics.totalExpenses.toLocaleString()}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Total Savings */}
        <Card className="bg-white/80 dark:bg-gray-800/80 border border-gray-200/50 dark:border-gray-700/50">
          <CardContent className="p-4 lg:p-6">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-xl flex items-center justify-center">
                <DollarSign className="w-5 h-5 text-white" />
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('Total Savings')}</p>
                <p className="text-xl font-bold text-gray-900 dark:text-white">
                  ${data.metrics.totalSavings.toLocaleString()}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Avg Monthly */}
        <Card className="bg-white/80 dark:bg-gray-800/80 border border-gray-200/50 dark:border-gray-700/50">
          <CardContent className="p-4 lg:p-6">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-r from-purple-500 to-violet-500 rounded-xl flex items-center justify-center">
                <Calendar className="w-5 h-5 text-white" />
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('avgMonthly')}</p>
                <p className="text-xl font-bold text-gray-900 dark:text-white">
                  ${data.metrics.avgMonthlyExpenses.toLocaleString()}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Savings Rate */}
        <Card className="bg-white/80 dark:bg-gray-800/80 border border-gray-200/50 dark:border-gray-700/50">
          <CardContent className="p-4 lg:p-6">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-r from-amber-500 to-orange-500 rounded-xl flex items-center justify-center">
                <Target className="w-5 h-5 text-white" />
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('savingsRate')}</p>
                <p className="text-xl font-bold text-gray-900 dark:text-white">
                  {data.metrics.savingsRate.toFixed(1)}%
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Monthly Overview */}
        <Card>
          <CardHeader>
            <CardTitle className="text-lg text-gray-900 dark:text-white flex items-center gap-2">
              <BarChart3 className="w-5 h-5" />
              {t('monthlyOverview')}
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={monthlyData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#374151" opacity={0.3} />
                  <XAxis dataKey="month" stroke="#6B7280" fontSize={12} />
                  <YAxis
                    stroke="#6B7280"
                    fontSize={12}
                    tickFormatter={(value) => `$${value.toLocaleString()}`}
                  />
                  <Tooltip content={<CustomTooltip />} />
                  <Legend />
                  <Bar dataKey="income" fill="#10B981" name={t('incomeLabel')} radius={[4, 4, 0, 0]} />
                  <Bar dataKey="expenses" fill="#EF4444" name={t('expensesLabel')} radius={[4, 4, 0, 0]} />
                  <Bar dataKey="savings" fill="#3B82F6" name={t('savingsLabel')} radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        {/* Expense Categories */}
        <Card>
          <CardHeader>
            <CardTitle className="text-lg text-gray-900 dark:text-white flex items-center gap-2">
              <PieChartIcon className="w-5 h-5" />
              {t('expenseCategories')}
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={data.categoryExpenses}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ category, percentage }) => `${category}: ${percentage}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="amount"
                  >
                    {data.categoryExpenses.map((entry) => (
                      <Cell key={`cell-${entry.category}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip content={<PieTooltip />} />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Budget vs Actual */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg text-gray-900 dark:text-white flex items-center gap-2">
            <Target className="w-5 h-5" />
            {t('budgetVsSpending')}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-80">
            {budgetComparison.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={budgetComparison}
                  margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
                  barCategoryGap={30}
                  barGap={2}
                >
                  <CartesianGrid strokeDasharray="3 3" stroke="#374151" opacity={0.3} />
                  <XAxis dataKey="category" stroke="#6B7280" fontSize={12} />
                  <YAxis
                    stroke="#6B7280"
                    fontSize={12}
                    tickFormatter={(value) => `$${value.toLocaleString()}`}
                  />
                  <Tooltip content={<CustomTooltip />} />
                  <Legend />
                  <Bar dataKey="budgeted" fill="#10B981" name={t('budgeted')} radius={[4, 4, 0, 0]} maxBarSize={20} />
                  <Bar dataKey="spent" fill="#EF4444" name={t('spent')} radius={[4, 4, 0, 0]} maxBarSize={20} />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="flex items-center justify-center h-full">
                <p className="text-gray-500 dark:text-gray-400">{t('No budget data available')}</p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Spending Trend */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg text-gray-900 dark:text-white flex items-center gap-2">
            <TrendingUp className="w-5 h-5" />
            {t('spendingTrend')}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" opacity={0.3} />
                <XAxis dataKey="month" stroke="#6B7280" fontSize={12} />
                <YAxis
                  stroke="#6B7280"
                  fontSize={12}
                  tickFormatter={(value) => `$${value.toLocaleString()}`}
                />
                <Tooltip content={<CustomTooltip />} />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="expenses"
                  stroke="#EF4444"
                  strokeWidth={3}
                  name={t('expensesLabel')}
                  dot={{ fill: '#EF4444', strokeWidth: 2, r: 6 }}
                />
                <Line
                  type="monotone"
                  dataKey="savings"
                  stroke="#10B981"
                  strokeWidth={3}
                  name={t('savingsLabel')}
                  dot={{ fill: '#10B981', strokeWidth: 2, r: 6 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
