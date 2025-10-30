import { z } from 'zod'
import i18n from '@/i18n'

export const createExpenseSchema = () => {
  return z.object({
    description: z
      .string()
      .min(1, i18n.t('descriptionRequired'))
      .max(100, i18n.t('descriptionTooLong')),
    category: z
      .string()
      .min(1, i18n.t('categoryRequired')),
    amount: z
      .coerce.number()
      .min(0.01, i18n.t('amountMin'))
      .max(999999, i18n.t('amountMax')),
    date: z
      .string()
      .min(1, i18n.t('dateRequired')),
    notes: z
      .string()
      .max(500, i18n.t('notesTooLong'))
      .optional(),
  })
}

export const createBudgetSchema = () => {
  return z.object({
    category: z
      .string()
      .min(1, i18n.t('categoryRequired')),
    limit: z
      .coerce.number()
      .min(0.01, i18n.t('budgetLimitMin'))
      .max(999999, i18n.t('budgetLimitMax')),
  })
}

export type ExpenseFormData = z.infer<ReturnType<typeof createExpenseSchema>>
export type BudgetFormData = z.infer<ReturnType<typeof createBudgetSchema>>
