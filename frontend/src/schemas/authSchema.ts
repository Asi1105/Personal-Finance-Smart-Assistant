import { z } from 'zod'
import i18n from '@/i18n'

export const loginSchema = z.object({
  email: z
    .string()
    .min(1, i18n.t('emailRequired'))
    .email(i18n.t('invalidEmail')),
  password: z
    .string()
    .min(1, i18n.t('passwordRequired'))
    .min(6, i18n.t('passwordMinLength')),
})

export const registerSchema = z
  .object({
    name: z
      .string()
      .min(1, i18n.t('nameRequired'))
      .min(2, i18n.t('nameMinLength')),
    email: z
      .string()
      .min(1, i18n.t('emailRequired'))
      .email(i18n.t('invalidEmail')),
    password: z
      .string()
      .min(1, i18n.t('passwordRequired'))
      .min(6, i18n.t('passwordMinLength')),
    confirmPassword: z
      .string()
      .min(1, i18n.t('confirmPasswordRequired')),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: i18n.t('passwordsDoNotMatch'),
    path: ['confirmPassword'],
  })

export type LoginFormData = z.infer<typeof loginSchema>
export type RegisterFormData = z.infer<typeof registerSchema>
