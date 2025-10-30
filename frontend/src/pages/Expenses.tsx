import { useState, useEffect } from 'react'
import { Link } from 'react-router'
import { format } from 'date-fns'
import {
  Plus,
  Search,
  Filter,
  Edit,
  Trash2,
  MoreHorizontal,
  ArrowUpDown,
  Calendar,
  DollarSign
} from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { StatsLoading, TableSkeleton } from '@/components/ui/loading'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

import { categories } from '@/data/staticData'
import { useExpenseStore } from '@/stores/expenseStore'
import { expenseApi } from '@/services/expenseService'
import { EditExpenseModal } from '@/components/EditExpenseModal'
import { useTranslation } from 'react-i18next'
import toast from 'react-hot-toast'

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

type SortField = 'date' | 'amount' | 'category' | 'description'
type SortDirection = 'asc' | 'desc'

export function Expenses() {
  const { t } = useTranslation()
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [selectedTimeFilter, setSelectedTimeFilter] = useState('all')
  const [sortField, setSortField] = useState<SortField>('date')
  const [sortDirection, setSortDirection] = useState<SortDirection>('desc')

  const { expenses, isLoading, fetchExpenses } = useExpenseStore()
  const [isDeleting, setIsDeleting] = useState(false)
  const [isEditModalOpen, setIsEditModalOpen] = useState(false)
  const [editingExpenseId, setEditingExpenseId] = useState<string | null>(null)
  useEffect(() => {
    fetchExpenses()
  }, [fetchExpenses])

  const filterByTime = (date: string, timeFilter: string) => {
    if (timeFilter === 'all') return true
    
    const expenseDate = new Date(date)
    const now = new Date()
    
    switch (timeFilter) {
      case 'today':
        return expenseDate.toDateString() === now.toDateString()
      case 'thisWeek': {
        const weekStart = new Date(now)
        weekStart.setDate(now.getDate() - now.getDay())
        weekStart.setHours(0, 0, 0, 0)
        return expenseDate >= weekStart
      }
      case 'thisMonth':
        return expenseDate.getMonth() === now.getMonth() && 
               expenseDate.getFullYear() === now.getFullYear()
      case 'lastMonth': {
        const lastMonth = new Date(now)
        lastMonth.setMonth(now.getMonth() - 1)
        return expenseDate.getMonth() === lastMonth.getMonth() && 
               expenseDate.getFullYear() === lastMonth.getFullYear()
      }
      case 'thisYear':
        return expenseDate.getFullYear() === now.getFullYear()
      default:
        return true
    }
  }
  const filteredAndSortedExpenses = expenses
    .filter((expense) => {
      const categoryDisplayName = expense.expenseCategory ? categoryEnumToDisplayName(expense.expenseCategory) : 'Other'
      const matchesSearch =
        expense.detail.toLowerCase().includes(searchTerm.toLowerCase()) ||
        categoryDisplayName.toLowerCase().includes(searchTerm.toLowerCase())
      const matchesCategory = selectedCategory === 'all' || categoryDisplayName === selectedCategory
      const matchesTimeFilter = filterByTime(expense.date, selectedTimeFilter)
      return matchesSearch && matchesCategory && matchesTimeFilter
    })
    .sort((a, b) => {
      let aValue: string | number
      let bValue: string | number

      switch (sortField) {
        case 'amount':
          aValue = a.amount
          bValue = b.amount
          break
        case 'category':
          aValue = a.expenseCategory ? categoryEnumToDisplayName(a.expenseCategory) : 'Other'
          bValue = b.expenseCategory ? categoryEnumToDisplayName(b.expenseCategory) : 'Other'
          break
        case 'description':
          aValue = a.detail
          bValue = b.detail
          break
        default:
          aValue = a.date
          bValue = b.date
      }

      if (typeof aValue === 'string' && typeof bValue === 'string') {
        return sortDirection === 'asc'
          ? aValue.localeCompare(bValue)
          : bValue.localeCompare(aValue)
      }

      return sortDirection === 'asc'
        ? (aValue as number) - (bValue as number)
        : (bValue as number) - (aValue as number)
    })

  const handleSort = (field: SortField) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc')
    } else {
      setSortField(field)
      setSortDirection('desc')
    }
  }

  const handleEditExpense = (expenseId: string) => {
    setEditingExpenseId(expenseId)
    setIsEditModalOpen(true)
  }

  const handleDeleteExpense = async (expenseId: string) => {
    if (!confirm('Are you sure you want to delete this expense?')) {
      return
    }

    setIsDeleting(true)
    try {
      await expenseApi.deleteExpense(expenseId)
      await fetchExpenses()
      toast.success('Expense deleted successfully')
    } catch (error: any) {
      toast.error(error.message || 'Failed to delete expense')
    } finally {
      setIsDeleting(false)
    }
  }

  const handleCloseEditModal = () => {
    setIsEditModalOpen(false)
    setEditingExpenseId(null)
  }

  const getCategoryIcon = (categoryName: string) => {
    const category = categories.find(c => c.name === categoryName)
    return category?.icon || '📦'
  }

  const getCategoryColor = (categoryName: string) => {
    const category = categories.find(c => c.name === categoryName)
    return category?.color || '#95a5a6'
  }

  const totalAmount = filteredAndSortedExpenses
    .filter(expense => expense.type === 'OUT')
    .reduce((sum, expense) => sum + expense.amount, 0)

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white">
            {t('expensesTitle')}
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mt-1">
            {t('expensesSubtitle')}
          </p>
        </div>
        <Link to="/expenses/add">
          <Button className="bg-gradient-to-r from-violet-500 to-purple-500 hover:from-violet-600 hover:to-purple-600 text-white rounded-2xl shadow-lg shadow-violet-500/25 hover:shadow-violet-500/40 transition-all duration-300 transform hover:scale-105">
            <Plus className="w-4 h-4 mr-2" />
            {t('addExpense')}
          </Button>
        </Link>
      </div>

      {/* Stats Cards */}
      {isLoading ? (
        <StatsLoading count={3} />
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 lg:gap-6">
          {/* Total Expenses */}
          <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
            <CardContent className="p-4 lg:p-6">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-xl flex items-center justify-center">
                  <DollarSign className="w-5 h-5 text-white" />
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{t('totalExpenses')}</p>
                  <p className="text-xl font-bold text-gray-900 dark:text-white">
                    ${totalAmount.toFixed(2)}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* This Month */}
          <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
            <CardContent className="p-4 lg:p-6">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gradient-to-r from-green-500 to-emerald-500 rounded-xl flex items-center justify-center">
                  <Calendar className="w-5 h-5 text-white" />
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{t('numberOfExpenses')}</p>
                  <p className="text-xl font-bold text-gray-900 dark:text-white">
                    {filteredAndSortedExpenses.length}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Categories */}
          <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
            <CardContent className="p-4 lg:p-6">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gradient-to-r from-amber-500 to-orange-500 rounded-xl flex items-center justify-center">
                  <Filter className="w-5 h-5 text-white" />
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{t('categories')}</p>
                  <p className="text-xl font-bold text-gray-900 dark:text-white">
                    {new Set(filteredAndSortedExpenses.map(e => e.expenseCategory)).size}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Filters */}
      <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
        <CardHeader>
          <CardTitle className="text-lg text-gray-900 dark:text-white">{t('filters')}</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col sm:flex-row gap-4">
            {/* Search */}
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
              <Input
                placeholder={t('searchExpenses')}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10 rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60"
              />
            </div>

            {/* Category Filter */}
            <Select value={selectedCategory} onValueChange={setSelectedCategory}>
              <SelectTrigger className="w-full sm:w-48 rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60">
                <SelectValue placeholder={t('allCategories')} />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{t('allCategories')}</SelectItem>
                {categories.map((category) => (
                  <SelectItem key={category.id} value={category.name}>
                    <div className="flex items-center gap-2">
                      <span>{category.icon}</span>
                      {category.name}
                    </div>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>

            {/* Time Filter */}
            <Select value={selectedTimeFilter} onValueChange={setSelectedTimeFilter}>
              <SelectTrigger className="w-full sm:w-48 rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60">
                <SelectValue placeholder={t('allTime')} />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{t('allTime')}</SelectItem>
                <SelectItem value="today">{t('today')}</SelectItem>
                <SelectItem value="thisWeek">{t('thisWeek')}</SelectItem>
                <SelectItem value="thisMonth">{t('thisMonth')}</SelectItem>
                <SelectItem value="lastMonth">{t('lastMonth')}</SelectItem>
                <SelectItem value="thisYear">{t('thisYear')}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Expenses Table */}
      <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
        <CardContent className="p-0">
          {isLoading ? (
            <div className="p-6">
              <TableSkeleton rows={8} columns={5} />
            </div>
          ) : (
            <div className="overflow-x-auto">
              <Table>
              <TableHeader>
                <TableRow className="border-b border-gray-200/50 dark:border-gray-700/50">
                  <TableHead className="px-6 py-4">
                    <Button
                      variant="ghost"
                      onClick={() => handleSort('description')}
                      className="h-auto p-0 font-medium text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
                    >
                      {t('expenseDetails')}
                      <ArrowUpDown className="w-4 h-4 ml-2" />
                    </Button>
                  </TableHead>
                  <TableHead className="px-6 py-4">
                    <Button
                      variant="ghost"
                      onClick={() => handleSort('date')}
                      className="h-auto p-0 font-medium text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
                    >
                      Date
                      <ArrowUpDown className="w-4 h-4 ml-2" />
                    </Button>
                  </TableHead>
                  <TableHead className="px-6 py-4 text-right">
                    <Button
                      variant="ghost"
                      onClick={() => handleSort('amount')}
                      className="h-auto p-0 font-medium text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
                    >
                      {t('amount')}
                      <ArrowUpDown className="w-4 h-4 ml-2" />
                    </Button>
                  </TableHead>
                  <TableHead className="px-6 py-4 w-16"></TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredAndSortedExpenses.map((expense) => {
                  const categoryDisplayName = expense.expenseCategory ? categoryEnumToDisplayName(expense.expenseCategory) : 'Other'
                  return (
                    <TableRow key={expense.id}>
                      <TableCell className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <div
                            className="w-12 h-12 rounded-xl flex items-center justify-center text-white text-lg shadow-md"
                            style={{
                              background: `linear-gradient(135deg, ${getCategoryColor(categoryDisplayName)}, ${getCategoryColor(categoryDisplayName)}CC)`
                            }}
                          >
                            {getCategoryIcon(categoryDisplayName)}
                          </div>
                          <div>
                            <div className="font-semibold text-gray-900 dark:text-white">
                              {expense.detail}
                            </div>
                            <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                              <span>{getCategoryIcon(categoryDisplayName)}</span>
                              {categoryDisplayName}
                            </div>
                          </div>
                        </div>
                      </TableCell>
                    <TableCell className="px-6 py-4">
                      <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-300">
                        <Calendar className="w-4 h-4" />
                        <span className="font-medium">
                          {format(new Date(expense.date), 'MMM dd, yyyy')}
                        </span>
                      </div>
                      <div className="text-xs text-gray-400 dark:text-gray-500 mt-1">
                        {format(new Date(expense.date), 'EEEE')}
                      </div>
                    </TableCell>
                    <TableCell className="px-6 py-4 text-right">
                      <div className="font-bold text-xl text-gray-900 dark:text-white">
                        ${expense.amount.toFixed(2)}
                      </div>
                    </TableCell>
                    <TableCell className="px-6 py-4">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="sm" className="h-8 w-8 p-0 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white">
                            <MoreHorizontal className="w-4 h-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" className="w-48">
                          <DropdownMenuItem 
                            className="flex items-center gap-2 cursor-pointer"
                            onClick={() => handleEditExpense(expense.id.toString())}
                          >
                            <Edit className="w-4 h-4" />
                            {t('edit')}
                          </DropdownMenuItem>
                          <DropdownMenuItem 
                            className="flex items-center gap-2 text-red-600 dark:text-red-400"
                            onClick={() => handleDeleteExpense(expense.id.toString())}
                            disabled={isDeleting}
                          >
                            <Trash2 className="w-4 h-4" />
                            {t('delete')}
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                  )
                })}
              </TableBody>
            </Table>

            {filteredAndSortedExpenses.length === 0 && (
              <div className="p-12 text-center">
                <div className="w-16 h-16 mx-auto mb-4 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center">
                  <Search className="w-8 h-8 text-gray-400" />
                </div>
                <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
                  {t('noExpensesFound')}
                </h3>
                <p className="text-gray-500 dark:text-gray-400 mb-4">
                  {t('tryAdjustingFilters')}
                </p>
                <Link to="/expenses/add">
                  <Button>{t('addFirstExpense')}</Button>
                </Link>
              </div>
            )}
          </div>
          )}
        </CardContent>
      </Card>

      {/* Edit Expense Modal */}
      {isEditModalOpen && (
        <EditExpenseModal
          isOpen={isEditModalOpen}
          onClose={handleCloseEditModal}
          expenseId={editingExpenseId}
        />
      )}
    </div>
  )
}
