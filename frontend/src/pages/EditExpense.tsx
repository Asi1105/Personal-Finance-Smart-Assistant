import { useState, useEffect } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowLeft, Save, DollarSign, Calendar, FileText, Tag, AlertCircle } from 'lucide-react'
import { useTranslation } from 'react-i18next'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
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
import { useExpenseStore } from '@/stores/expenseStore'
import { createExpenseSchema, type ExpenseFormData } from '@/schemas/expenseSchema'
import toast from 'react-hot-toast'

export function EditExpense() {
  const { t } = useTranslation()
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [, setExpense] = useState<any>(null)
  const { fetchExpenses } = useExpenseStore()

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

  // Load expense data
  useEffect(() => {
    const loadExpense = async () => {
      if (!id) {
        setError('Expense ID is required')
        setIsLoading(false)
        return
      }

      try {
        const expenseData = await expenseApi.getExpense(id)
        setExpense(expenseData)
        
        // Populate form with expense data
        form.reset({
          description: expenseData.detail,
          category: expenseData.expenseCategory || '',
          amount: expenseData.amount,
          date: expenseData.date,
          notes: expenseData.note || '', // Use note field from Transaction
        })
      } catch (error: any) {
        setError(error.message || 'Failed to load expense')
      } finally {
        setIsLoading(false)
      }
    }

    loadExpense()
  }, [id, form])

  const onSubmit = async (data: ExpenseFormData) => {
    if (!id) return
    
    setIsSubmitting(true)
    
    try {
      const expenseRequest: ExpenseRequest = {
        description: data.description,
        category: data.category,
        amount: data.amount,
        date: data.date,
        notes: data.notes,
      }
      
      await expenseApi.updateExpense(id, expenseRequest)
      await fetchExpenses() // Refresh the expenses list
      
      toast.success('Expense updated successfully')
      navigate('/expenses')
    } catch (error: any) {
      toast.error(error.message || 'Failed to update expense')
    } finally {
      setIsSubmitting(false)
    }
  }


  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="text-center">
          <div className="w-8 h-8 border-2 border-violet-500 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600 dark:text-gray-300">{t('loadingExpense')}</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="space-y-6 max-w-2xl mx-auto">
        <div className="flex items-center gap-4">
          <Link to="/expenses">
            <Button variant="outline" size="sm" className="bg-white/80 dark:bg-gray-800/80">
              <ArrowLeft className="w-4 h-4 mr-2" />
              {t('back')}
            </Button>
          </Link>
          <div>
            <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white">
              {t('editExpense')}
            </h1>
            <p className="text-gray-600 dark:text-gray-300 mt-1">
              {t('updateExpenseDetails')}
            </p>
          </div>
        </div>

        <Card className="bg-white/80 dark:bg-gray-800/80">
          <CardContent className="p-8 text-center">
            <div className="w-16 h-16 mx-auto mb-4 bg-red-100 dark:bg-red-900/30 rounded-full flex items-center justify-center">
              <AlertCircle className="w-8 h-8 text-red-600 dark:text-red-400" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
              {error}
            </h3>
            <p className="text-gray-500 dark:text-gray-400 mb-6">
              {t('expenseNotFoundDesc')}
            </p>
            <Link to="/expenses">
              <Button>{t('returnToExpenses')}</Button>
            </Link>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="space-y-6 max-w-2xl mx-auto">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Link to="/expenses">
          <Button variant="outline" size="sm" className="bg-white/80 dark:bg-gray-800/80">
            <ArrowLeft className="w-4 h-4 mr-2" />
            {t('back')}
          </Button>
        </Link>
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white">
            {t('editExpense')}
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mt-1">
            {t('updateExpenseDetails')}
          </p>
        </div>
      </div>

      {/* Form */}
      <Card className="bg-white/80 dark:bg-gray-800/80">
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
                    <FormLabel className="flex items-center gap-2">
                      <FileText className="w-4 h-4" />
                      {t('description')}
                    </FormLabel>
                    <FormControl>
                      <Input
                        {...field}
                        placeholder={t('descriptionPlaceholder')}
                        className="rounded-xl bg-white/50 dark:bg-gray-700/50"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Category + Amount */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <FormField
                  control={form.control}
                  name="category"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-2">
                        <Tag className="w-4 h-4" />
                        {t('category')}
                      </FormLabel>
                      <Select onValueChange={field.onChange} value={field.value}>
                        <FormControl>
                          <SelectTrigger className="rounded-xl bg-white/50 dark:bg-gray-700/50">
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

                <FormField
                  control={form.control}
                  name="amount"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-2">
                        <DollarSign className="w-4 h-4" />
                        {t('amount')}
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <div className="absolute left-3 top-1/2 transform -translate-y-1/2">$</div>
                          <Input
                            type="number"
                            step="0.01"
                            {...field}
                            onChange={(e) => field.onChange(Number.parseFloat(e.target.value) || 0)}
                            placeholder="0.00"
                            className="pl-8 rounded-xl bg-white/50 dark:bg-gray-700/50"
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
                    <FormLabel className="flex items-center gap-2">
                      <Calendar className="w-4 h-4" />
                      {t('date')}
                    </FormLabel>
                    <FormControl>
                      <Input type="date" {...field} className="rounded-xl bg-white/50 dark:bg-gray-700/50" />
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
                    <FormLabel>{t('notesOptional')}</FormLabel>
                    <FormControl>
                      <Textarea
                        {...field}
                        placeholder={t('notesPlaceholder')}
                        className="rounded-xl bg-white/50 dark:bg-gray-700/50"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Submit */}
              <div className="flex flex-col sm:flex-row gap-3 pt-4">
                <Link to="/expenses" className="flex-1 sm:flex-initial">
                  <Button variant="outline" className="w-full bg-white/80 dark:bg-gray-800/80">
                    {t('cancel')}
                  </Button>
                </Link>
                <Button
                  type="submit"
                  disabled={isSubmitting}
                  className="flex-1 bg-gradient-to-r from-violet-500 to-purple-500 text-white"
                >
                  {isSubmitting ? (
                    <div className="flex items-center gap-2">
                      <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                      {t('updating')}
                    </div>
                  ) : (
                    <>
                      <Save className="w-4 h-4 mr-2" />
                      {t('updateExpense')}
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
