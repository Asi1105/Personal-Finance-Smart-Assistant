import { useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Save, DollarSign, Calendar, FileText } from 'lucide-react'
import { useTranslation } from 'react-i18next'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
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
import { expenseApi, type ExpenseRequest } from '@/services/expenseService'
import { useExpenseStore } from '@/stores/expenseStore'
import toast from 'react-hot-toast'

const expenseSchema = z.object({
  description: z
    .string()
    .min(1, 'Description is required')
    .max(100, 'Description must be less than 100 characters'),
  category: z.string().min(1, 'Category is required'),
  amount: z
    .number()
    .min(0.01, 'Amount must be greater than 0')
    .max(999999, 'Amount must be less than 1,000,000'),
  date: z.string().min(1, 'Date is required'),
  notes: z
    .string()
    .max(500, 'Notes must be less than 500 characters')
    .optional(),
})

type ExpenseFormData = z.infer<typeof expenseSchema>

interface EditExpenseModalProps {
  readonly isOpen: boolean
  readonly onClose: () => void
  readonly expenseId: string | null
}

export function EditExpenseModal({
  isOpen,
  onClose,
  expenseId,
}: EditExpenseModalProps) {
  const { t } = useTranslation()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const { fetchExpenses } = useExpenseStore()

  const form = useForm<ExpenseFormData>({
    resolver: zodResolver(expenseSchema),
    defaultValues: {
      description: '',
      category: '',
      amount: 0,
      date: new Date().toISOString().split('T')[0],
      notes: '',
    },
  })

  useEffect(() => {
    const loadExpense = async () => {
      if (!expenseId || !isOpen) return
      setIsLoading(true)
      setError(null)
      try {
        const expenseData = await expenseApi.getExpense(expenseId)
        form.reset({
          description: expenseData.detail,
          category: expenseData.expenseCategory || '',
          amount: expenseData.amount,
          date: expenseData.date,
          notes: expenseData.note || '',
        })
      } catch (error: any) {
        setError(error.message || t('failedToLoadExpense'))
      } finally {
        setIsLoading(false)
      }
    }

    loadExpense()
  }, [expenseId, isOpen, form, t])

  const onSubmit = async (data: ExpenseFormData) => {
    if (!expenseId) return
    setIsSubmitting(true)
    try {
      const expenseRequest: ExpenseRequest = {
        description: data.description,
        category: data.category,
        amount: data.amount,
        date: data.date,
        notes: data.notes,
      }

      await expenseApi.updateExpense(expenseId, expenseRequest)
      await fetchExpenses()
      toast.success(t('expenseUpdatedSuccessfully'))
      onClose()
    } catch (error: any) {
      toast.error(error.message || t('failedToUpdateExpense'))
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
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold text-gray-900 dark:text-white">
            {t('editExpenseTitle')}
          </DialogTitle>
        </DialogHeader>

        {(() => {
          if (isLoading) {
            return (
              <div className="flex items-center justify-center py-8">
                <div className="text-center">
                  <div className="w-8 h-8 border-2 border-violet-500 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                  <p className="text-gray-600 dark:text-gray-300">
                    {t('loadingExpense')}
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
                {/* Description */}
                <FormField
                  control={form.control}
                  name="description"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="text-gray-700 dark:text-gray-300">
                        {t('description')}
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <FileText className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                          <Input
                            placeholder={t('enterExpenseDescription')}
                            className="pl-10 rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60"
                            {...field}
                          />
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

              {/* Category */}
              <FormField
                control={form.control}
                name="category"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-gray-700 dark:text-gray-300">
                      Category
                    </FormLabel>
                    <Select onValueChange={field.onChange} value={field.value}>
                      <FormControl>
                        <SelectTrigger className="rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60">
                          <SelectValue placeholder="Select a category">
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
                      <FormLabel className="text-gray-700 dark:text-gray-300">
                        {t('amount')}
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
                              field.onChange(
                                Number.parseFloat(e.target.value) || 0
                              )
                            }
                          />
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {/* Date */}
                <FormField
                  control={form.control}
                  name="date"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="text-gray-700 dark:text-gray-300">
                        {t('date')}
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                          <Input
                            type="date"
                            className="pl-10 rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60"
                            {...field}
                          />
                        </div>
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
                          placeholder={t('addAdditionalNotes')}
                          className="rounded-xl bg-white/50 dark:bg-gray-700/50 border-gray-200/60 dark:border-gray-600/60 resize-none"
                          rows={3}
                          {...field}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {/* Submit Button */}
                <div className="flex gap-3 pt-4">
                  <Button
                    type="submit"
                    disabled={isSubmitting}
                    className="flex-1 bg-gradient-to-r from-violet-500 to-purple-500 hover:from-violet-600 hover:to-purple-600 text-white rounded-xl shadow-lg shadow-violet-500/25 hover:shadow-violet-500/40 transition-all duration-300"
                  >
                    {isSubmitting ? (
                      <div className="flex items-center gap-2">
                        <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                        {t('updating')}
                      </div>
                    ) : (
                      <div className="flex items-center gap-2">
                        <Save className="w-4 h-4" />
                        {t('updateExpense')}
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
