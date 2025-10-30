import { useState, useEffect } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { ArrowLeft, Save, DollarSign, Tag, AlertCircle } from 'lucide-react'
import { useTranslation } from 'react-i18next'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
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
import { budgetApi, type BudgetRequest } from '@/services/budgetService'
import { useBudgetStore } from '@/stores/budgetStore'
import toast from 'react-hot-toast'

const budgetSchema = z.object({
  category: z.string().min(1, 'Category is required'),
  amount: z.number().min(0.01, 'Amount must be greater than 0').max(999999, 'Amount must be less than 1,000,000'),
})

type BudgetFormData = z.infer<typeof budgetSchema>

/*
  Page for editing an existing budget
  Loads budget data from URL parameter and allows updating
 */
export function EditBudget() {
  const { t } = useTranslation()
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const { fetchBudgets } = useBudgetStore()

  const form = useForm<BudgetFormData>({
    resolver: zodResolver(budgetSchema),
    defaultValues: {
      category: '',
      amount: 0,
    },
  })

  // Load the existing budget data when page opens
  useEffect(() => {
    const loadBudget = async () => {
      if (!id) {
        setError('Budget ID is required')
        setIsLoading(false)
        return
      }

      try {
        const budgetData = await budgetApi.getBudget(Number.parseInt(id))

        // Fill the form with existing budget data
        form.reset({
          category: budgetData.category,
          amount: budgetData.amount,
        })
      } catch (error: any) {
        setError(error.message || 'Failed to load budget')
      } finally {
        setIsLoading(false)
      }
    }

    loadBudget()
  }, [id, form])

  // Handle form submission to update budget
  const onSubmit = async (data: BudgetFormData) => {
    if (!id) return
    setIsSubmitting(true)
    try {
      const budgetRequest: BudgetRequest = {
        category: data.category,
        amount: data.amount,
        period: 'monthly', // Keep period as monthly by default
      }
      
      await budgetApi.updateBudget(Number.parseInt(id), budgetRequest)
      await fetchBudgets()
      toast.success(t('budgetUpdatedSuccessfully'))
      navigate('/budgets')
    } catch (error: any) {
      toast.error(t('failedToUpdateBudget'))
    } finally {
      setIsSubmitting(false)
    }
  }


  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="text-center">
          <div className="w-8 h-8 border-2 border-violet-500 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600 dark:text-gray-300">{t('loadingBudget')}</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="text-center">
          <AlertCircle className="w-12 h-12 text-red-500 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
            {t('errorLoadingBudget')}
          </h3>
          <p className="text-gray-600 dark:text-gray-300 mb-4">{error}</p>
          <Link to="/budgets">
            <Button variant="outline">{t('backToBudgets')}</Button>
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Link to="/budgets">
          <Button variant="outline" size="sm" className="flex items-center gap-2">
            <ArrowLeft className="w-4 h-4" />
            {t('back')}
          </Button>
        </Link>
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white">
            {t('editBudgetTitle')}
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mt-1">
            {t('editBudgetSubtitle')}
          </p>
        </div>
      </div>

      <div className="max-w-2xl">
        <Card className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl border border-gray-200/50 dark:border-gray-700/50">
          <CardHeader>
            <CardTitle className="text-lg text-gray-900 dark:text-white flex items-center gap-2">
              <Tag className="w-5 h-5" />
              {t('budgetInformation')}
            </CardTitle>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                <FormField
                  control={form.control}
                  name="category"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="text-gray-700 dark:text-gray-300">
                        {t('category')}
                      </FormLabel>
                      <Select onValueChange={field.onChange} defaultValue={field.value}>
                        <FormControl>
                          <SelectTrigger className="rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60">
                            <SelectValue placeholder={t('selectCategory')} />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
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
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="amount"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="text-gray-700 dark:text-gray-300">
                        {t('budgetAmount')}
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                          <Input
                            type="number"
                            step="0.01"
                            min="0.01"
                            max="999999"
                            placeholder="0.00"
                            className="pl-10 rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60 [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none [-moz-appearance:textfield]"
                            {...field}
                            value={field.value ?? ''}
                            onChange={(e) => {
                              const value = e.target.value
                              if (value === '' || value === null) {
                                field.onChange(0.01)
                                return
                              }
                              const numValue = Number.parseFloat(value)
                              if (!Number.isNaN(numValue) && numValue >= 0) {
                                const roundedValue = Math.round(numValue * 100) / 100
                                field.onChange(Math.max(0.01, Math.min(roundedValue, 999999)))
                              }
                            }}
                          />
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />


                <div className="flex gap-3 pt-4">
                  <Button
                    type="submit"
                    disabled={isSubmitting}
                    className="flex-1 bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-600 hover:to-cyan-600 text-white rounded-xl shadow-lg shadow-emerald-500/25 hover:shadow-emerald-500/40 transition-all duration-300"
                  >
                    {isSubmitting ? (
                      <div className="flex items-center gap-2">
                        <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                        {t('updating')}
                      </div>
                    ) : (
                      <div className="flex items-center gap-2">
                        <Save className="w-4 h-4" />
                        {t('updateBudget')}
                      </div>
                    )}
                  </Button>
                  <Link to="/budgets">
                    <Button
                      type="button"
                      variant="outline"
                      className="px-6 rounded-xl"
                    >
                      {t('cancel')}
                    </Button>
                  </Link>
                </div>
              </form>
            </Form>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
