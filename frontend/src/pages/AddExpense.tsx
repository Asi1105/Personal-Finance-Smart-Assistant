import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { format } from 'date-fns'
import { ArrowLeft, Save, DollarSign, Calendar, FileText, Tag, Receipt } from 'lucide-react'
import toast from 'react-hot-toast'
import { useTranslation } from 'react-i18next'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Spinner } from '@/components/ui/loading'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

import { categories } from '@/data/staticData'
import { expenseApi, type ExpenseRequest } from '@/services/expenseService'
import { useDashboardStore } from '@/stores/dashboardStore'
import { useBudgetStore } from '@/stores/budgetStore'
import { createExpenseSchema, type ExpenseFormData } from '@/schemas/expenseSchema'

export function AddExpense() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const { fetchDashboardStats } = useDashboardStore()
  const { fetchBudgets } = useBudgetStore()

  const form = useForm<ExpenseFormData>({
    resolver: zodResolver(createExpenseSchema()),
    defaultValues: {
      description: '',
      category: '',
      amount: 0,
      date: new Date().toISOString().split('T')[0],
      notes: '',
    },
  })

  const onSubmit = async (data: ExpenseFormData) => {
    setIsSubmitting(true)
    try {
      const expenseRequest: ExpenseRequest = {
        description: data.description,
        category: data.category,
        amount: data.amount,
        date: data.date,
        notes: data.notes || undefined,
      }

      await expenseApi.addExpense(expenseRequest)
      await Promise.all([fetchDashboardStats(), fetchBudgets()])

      toast.success(t('expenseAdded'))
      navigate('/expenses')
    } catch (error: any) {
      toast.error(error.message || t('expenseAddFailed'))
    } finally {
      setIsSubmitting(false)
    }
  }

  const getCategoryIcon = (categoryEnum: string) => {
    // Map enum values to category names
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

    const categoryName = categoryMap[categoryEnum] || 'Other'
    const category = categories.find(c => c.name === categoryName)
    return category?.icon || '📦'
  }

  const getCategoryColor = (categoryEnum: string) => {
    // Map enum values to category names
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

    const categoryName = categoryMap[categoryEnum] || 'Other'
    const category = categories.find(c => c.name === categoryName)
    return category?.color || '#95a5a6'
  }

  return (
    <div className="space-y-6 max-w-2xl mx-auto">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Link to="/expenses">
          <Button
            variant="outline"
            size="sm"
            className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50 hover:bg-gray-50/80 dark:hover:bg-gray-700/80"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            {t('back')}
          </Button>
        </Link>
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white">
            {t('addExpense')}
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mt-1">
            {t('recordNewExpense')}
          </p>
        </div>
      </div>

      {/* Form */}
      <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
        <CardHeader>
          <CardTitle className="text-lg text-gray-900 dark:text-white flex items-center gap-2">
            <div className="w-8 h-8 bg-gradient-to-r from-violet-500 to-purple-500 rounded-lg flex items-center justify-center">
              <DollarSign className="w-4 h-4 text-white" />
            </div>
            {t('expenseDetails')}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              {/* Description */}
              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-gray-700 dark:text-gray-300 flex items-center gap-2">
                      <FileText className="w-4 h-4" />
                      {t('description')}
                    </FormLabel>
                    <FormControl>
                      <Input
                        {...field}
                        placeholder={t('descriptionPlaceholder')}
                        className="rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Category + Amount */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Category */}
                <FormField
                  control={form.control}
                  name="category"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="text-gray-700 dark:text-gray-300 flex items-center gap-2">
                        <Tag className="w-4 h-4" />
                        {t('category')}
                      </FormLabel>
                      <Select onValueChange={field.onChange} value={field.value}>
                        <FormControl>
                          <SelectTrigger className="rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60">
                            <SelectValue placeholder={t('selectCategory')}>
                              {field.value && (() => {
                                // Map enum value to category name
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
                                const categoryName = categoryMap[field.value] || 'Other'
                                const category = categories.find(c => c.name === categoryName)

                                return category ? (
                                  <div className="flex items-center gap-2">
                                    <span>{category.icon}</span>
                                    {category.name}
                                  </div>
                                ) : field.value
                              })()}
                            </SelectValue>
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
                          {categories.map((category) => {
                            // Map category name to enum value
                            const categoryMap: Record<string, string> = {
                              'Food & Dining': 'FOOD_DINING',
                              'Transportation': 'TRANSPORTATION',
                              'Entertainment': 'ENTERTAINMENT',
                              'Shopping': 'SHOPPING',
                              'Bills & Utilities': 'BILLS_UTILITIES',
                              'Healthcare': 'HEALTHCARE',
                              'Travel': 'TRAVEL',
                              'Education': 'EDUCATION',
                              'Other': 'OTHER'
                            }
                            const enumValue = categoryMap[category.name] || 'OTHER'

                            return (
                              <SelectItem key={category.id} value={enumValue}>
                                <div className="flex items-center gap-2">
                                  <span>{category.icon}</span>
                                  {category.name}
                                </div>
                              </SelectItem>
                            )
                          })}
                        </SelectContent>
                      </Select>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {/* Amount */}
                <FormField
                  control={form.control}
                  name="amount"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="text-gray-700 dark:text-gray-300 flex items-center gap-2">
                        <DollarSign className="w-4 h-4" />
                        {t('amount')}
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500 dark:text-gray-400">$</div>
                          <Input
                            type="number"
                            step="0.01"
                            min="0"
                            max="999999"
                            {...field}
                            onChange={(e) => field.onChange(Number.parseFloat(e.target.value) || 0)}
                            placeholder="0.00"
                            className="pl-8 rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60"
                          />
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              {/* Date */}
              <FormField
                control={form.control}
                name="date"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-gray-700 dark:text-gray-300 flex items-center gap-2">
                      <Calendar className="w-4 h-4" />
                      {t('date')}
                    </FormLabel>
                    <FormControl>
                      <Input
                        type="date"
                        {...field}
                        lang="en"
                        className="rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Notes */}
              <FormField
                control={form.control}
                name="notes"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-gray-700 dark:text-gray-300">
                      {t('notesOptional')}
                    </FormLabel>
                    <FormControl>
                      <Textarea
                        {...field}
                        placeholder={t('notesPlaceholder')}
                        className="rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60 min-h-20"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Preview */}
              {form.watch('category') && form.watch('amount') > 0 && (
                <Card className="bg-gradient-to-r from-violet-50 to-purple-50 dark:from-violet-900/20 dark:to-purple-900/20 border border-violet-200/50 dark:border-violet-700/50 shadow-lg shadow-violet-500/10">
                  <CardContent className="p-6">
                    {/* Header */}
                    <div className="flex items-center gap-2 mb-3">
                      <Receipt className="w-4 h-4 text-violet-600 dark:text-violet-400" />
                      <p className="text-sm font-medium text-violet-800 dark:text-violet-300">
                        {t('expensePreview')}
                      </p>
                    </div>

                    {/* Main preview panel */}
                    <div className="flex items-center justify-between p-4 bg-white/60 dark:bg-gray-800/60 rounded-xl">
                      {/* Left: Category info */}
                      <div className="flex items-center gap-3 flex-1">
                        <div
                          className="w-12 h-12 rounded-xl flex items-center justify-center text-white text-lg shadow-md"
                          style={{
                            background: `linear-gradient(135deg, ${getCategoryColor(form.watch('category') || 'OTHER')}, ${getCategoryColor(form.watch('category') || 'OTHER')}CC)`
                          }}
                        >
                          {getCategoryIcon(form.watch('category') || 'OTHER')}
                        </div>
                        <div>
                          <div className="font-semibold text-gray-900 dark:text-white">
                            {form.watch('description') || t('newExpense')}
                          </div>
                          <div className="text-sm text-gray-500 dark:text-gray-400">
                            {(() => {
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
                              return categoryMap[form.watch('category')] || 'Other'
                            })()}
                          </div>
                        </div>
                      </div>

                      {/* Center: Date */}
                      {form.watch('date') && (
                        <div className="px-8 py-4 text-center">
                          <div className="flex items-center justify-center gap-2 text-sm text-gray-600 dark:text-gray-300">
                            <Calendar className="w-4 h-4" />
                            <span className="font-medium">
                              {format(new Date(form.watch('date')), 'MMM dd, yyyy')}
                            </span>
                          </div>
                          <div className="text-xs text-gray-400 dark:text-gray-500 mt-1">
                            {format(new Date(form.watch('date')), 'EEEE')}
                          </div>
                        </div>
                      )}

                      {/* Right: Amount */}
                      <div className="text-right">
                        <div className="font-bold text-xl text-gray-900 dark:text-white">
                          ${form.watch('amount').toFixed(2)}
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              )}

              {/* Buttons */}
              <div className="flex flex-col sm:flex-row gap-3 pt-4">
                <Link to="/expenses" className="flex-1 sm:flex-initial">
                  <Button
                    type="button"
                    variant="outline"
                    className="w-full bg-white/80 dark:bg-gray-800/80 border border-gray-200/50 dark:border-gray-700/50"
                  >
                    {t('cancel')}
                  </Button>
                </Link>
                <Button
                  type="submit"
                  disabled={isSubmitting}
                  className="flex-1 bg-gradient-to-r from-violet-500 to-purple-500 text-white rounded-2xl shadow-lg hover:scale-105 transition-all duration-300 disabled:transform-none"
                >
                  {isSubmitting ? (
                    <div className="flex items-center gap-2">
                      <Spinner size="sm" className="border-white/30 border-t-white" />
                      {t('saving')}
                    </div>
                  ) : (
                    <>
                      <Save className="w-4 h-4 mr-2" />
                      {t('addExpense')}
                    </>
                  )}
                </Button>
              </div>
            </form>
          </Form>
        </CardContent>
      </Card>
    </div>
  )
}
