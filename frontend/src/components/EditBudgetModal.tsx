import { useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Save, DollarSign } from 'lucide-react'
import { useTranslation } from 'react-i18next'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
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
  amount: z
    .number()
    .min(0.01, 'Amount must be greater than 0')
    .max(999999, 'Amount must be less than 1,000,000'),
})

type BudgetFormData = z.infer<typeof budgetSchema>

interface EditBudgetModalProps {
  readonly isOpen: boolean
  readonly onClose: () => void
  readonly budgetId: number | null
}

export function EditBudgetModal({ isOpen, onClose, budgetId }: EditBudgetModalProps) {
  const { t } = useTranslation()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const { fetchBudgets, updateBudget } = useBudgetStore()

  const form = useForm<BudgetFormData>({
    resolver: zodResolver(budgetSchema),
    defaultValues: {
      category: '',
      amount: 0,
    },
  })

  useEffect(() => {
    const loadBudget = async () => {
      if (!budgetId || !isOpen) return

      setIsLoading(true)
      setError(null)

      try {
        const budgetData = await budgetApi.getBudget(budgetId)
        
        // Populate form with budget data
        form.reset({
          category: budgetData.category,
          amount: budgetData.amount,
        })
      } catch (error: any) {
        setError(error.message || t('failedToLoadBudget'))
      } finally {
        setIsLoading(false)
      }
    }

    loadBudget()
  }, [budgetId, isOpen, form, t])

  const onSubmit = async (data: BudgetFormData) => {
    if (!budgetId) return
    
    setIsSubmitting(true)
    
    try {
      const budgetRequest: BudgetRequest = {
        category: data.category,
        amount: data.amount,
        period: 'monthly', // Keep period as monthly by default
      }

      await updateBudget(budgetId, budgetRequest)
      onClose()
    } catch (error: any) {
      // Error handling is done in the store
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleClose = () => {
    form.reset()
    setError(null)
    onClose()
  }

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold text-gray-900 dark:text-white">
            {t('editBudgetTitle')}
          </DialogTitle>
        </DialogHeader>

        {(() => {
          if (isLoading) {
            return (
              <div className="flex items-center justify-center py-8">
                <div className="text-center">
                  <div className="w-8 h-8 border-2 border-violet-500 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                  <p className="text-gray-600 dark:text-gray-300">
                    {t('loadingBudget')}
                  </p>
                </div>
              </div>
            )
          }

          if (error) {
            return (
              <div className="text-center py-8">
                <p className="text-red-600 dark:text-red-400">{error}</p>
                <Button variant="outline" onClick={handleClose} className="mt-4">
                  {t('close')}
                </Button>
              </div>
            )
          }

          return (
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
                            placeholder="0.00"
                            className="pl-10 rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60"
                            {...field}
                            onChange={(e) =>
                              field.onChange(Number.parseFloat(e.target.value) || 0)
                            }
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
                  <Button
                    type="button"
                    variant="outline"
                    onClick={handleClose}
                    className="px-6 rounded-xl"
                  >
                    {t('cancel')}
                  </Button>
                </div>
              </form>
            </Form>
          )
        })()}
      </DialogContent>
    </Dialog>
  )
}
