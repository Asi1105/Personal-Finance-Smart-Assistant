import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowLeft, Save, Target, DollarSign, Tag, Calendar } from 'lucide-react'
import { useTranslation } from 'react-i18next'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
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
import { budgetApi, type BudgetRequest } from '@/services/budgetService'
import { useDashboardStore } from '@/stores/dashboardStore'
import toast from 'react-hot-toast'
import { z } from 'zod'

const createBudgetSchema = (t: any) =>
  z.object({
    category: z
      .string()
      .min(1, t('categoryRequired')),
    limit: z
      .coerce.number()
      .min(0.01, t('budgetLimitMin'))
      .max(999999, t('budgetLimitMax')),
  })

type BudgetFormData = z.infer<ReturnType<typeof createBudgetSchema>>
export function AddBudget() {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const { fetchDashboardStats } = useDashboardStore()

  const form = useForm<BudgetFormData>({
    resolver: zodResolver(createBudgetSchema(t)),
    defaultValues: {
      category: '',
      limit: 0, //
    },
  })

  // Handle form submission
  const onSubmit = async (data: BudgetFormData) => {
    setIsSubmitting(true)

    try {
      const budgetRequest: BudgetRequest = {
        category: data.category,
        amount: data.limit,
        period: 'monthly',
      }

      await budgetApi.addBudget(budgetRequest)
      await fetchDashboardStats()

      toast.success(t('budgetAddedSuccess'))
      navigate('/budgets')
    } catch (error: any) {
      toast.error(error.message || t('budgetAddFailed'))
    } finally {
      setIsSubmitting(false)
    }
  }

  // Get category icon for preview
  const getCategoryIcon = (categoryName: string) => {
    const category = categories.find(c => c.name === categoryName)
    return category?.icon || '📦'
  }

  return (
    <div className="space-y-6 max-w-2xl mx-auto">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Link to="/budgets">
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
            {t('addBudget')}
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mt-1">
            {t('setSpendingLimit')}
          </p>
        </div>
      </div>

      {/* Form */}
      <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
        <CardHeader>
          <CardTitle className="text-lg text-gray-900 dark:text-white flex items-center gap-2">
            <div className="w-8 h-8 bg-gradient-to-r from-emerald-500 to-cyan-500 rounded-lg flex items-center justify-center">
              <Target className="w-4 h-4 text-white" />
            </div>
            {t('budgetDetails')}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
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
                        {categories.map(category => {
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

              {/* Budget Limit */}
              <FormField
                control={form.control}
                name="limit"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-gray-700 dark:text-gray-300 flex items-center gap-2">
                      <DollarSign className="w-4 h-4" />
                      {t('budgetLimit')}
                    </FormLabel>
                    <FormControl>
                      <div className="relative">
                        <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500 dark:text-gray-400">
                          $
                        </div>
                        <Input
                          type="number"
                          step="0.01"
                          min="0"
                          max="999999"
                          {...field}
                          onChange={e => field.onChange(e.target.value === '' ? 0 : Number.parseFloat(e.target.value))}
                          placeholder="0.00"
                          className="pl-8 rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60"
                        />
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Preview */}
              {form.watch('category') && form.watch('limit') > 0 && (
                <Card className="bg-gradient-to-r from-emerald-50 to-cyan-50 dark:from-emerald-900/20 dark:to-cyan-900/20 border border-emerald-200/50 dark:border-emerald-700/50 shadow-lg shadow-emerald-500/10">
                  <CardContent className="p-6">
                    <div className="flex items-center gap-2 mb-3">
                      <Target className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
                      <p className="text-sm font-medium text-emerald-800 dark:text-emerald-300">
                        {t('budgetPreview')}
                      </p>
                    </div>
                    <div className="flex items-center justify-between p-4 bg-white/60 dark:bg-gray-800/60 rounded-xl border border-white/80 dark:border-gray-700/80">
                      <div className="flex items-center gap-3">
                        <div className="w-12 h-12 bg-gradient-to-r from-emerald-500 to-cyan-500 rounded-xl flex items-center justify-center text-white text-lg shadow-md">
                          {getCategoryIcon(form.watch('category'))}
                        </div>
                        <div>
                          <p className="font-semibold text-gray-900 dark:text-white">
                            {form.watch('category')} {t('budget')}
                          </p>
                          <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                            <Calendar className="w-3 h-3" />
                            {t('monthly')}
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="font-bold text-xl text-gray-900 dark:text-white">
                          ${form.watch('limit').toFixed(2)}
                        </div>
                        <div className="text-xs text-emerald-600 dark:text-emerald-400 font-medium">
                          {t('zeroSpent')}
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              )}

              {/* Buttons */}
              <div className="flex flex-col sm:flex-row gap-3 pt-4">
                <Link to="/budgets" className="flex-1 sm:flex-initial">
                  <Button
                    type="button"
                    variant="outline"
                    className="w-full bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50"
                  >
                    {t('cancel')}
                  </Button>
                </Link>
                <Button
                  type="submit"
                  disabled={isSubmitting}
                  className="flex-1 bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-600 hover:to-cyan-600 text-white rounded-2xl shadow-lg"
                >
                  {isSubmitting ? (
                    <div className="flex items-center gap-2">
                      <Spinner size="sm" className="border-white/30 border-t-white" />
                      {t('saving')}
                    </div>
                  ) : (
                    <>
                      <Save className="w-4 h-4 mr-2" />
                      {t('addBudget')}
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
