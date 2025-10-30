import { useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { format } from 'date-fns'
import { useTranslation } from 'react-i18next'
import {
  PiggyBank,
  Target,
  DollarSign,
  Plus,
  Minus,
  History
} from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Spinner } from '@/components/ui/loading'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { useDashboardStore } from '@/stores/dashboardStore'
import { savingApi, type SaveMoneyRequest, type UnsaveMoneyRequest, type SavingLog } from '@/services/savingService'
import { saveGoalApi, type SaveGoalRequest } from '@/services/saveGoalService'
import toast from 'react-hot-toast'

const saveGoalSchema = z.object({
  targetAmount: z.number().min(0.01, 'Target amount must be greater than 0'),
  description: z.string().optional()
})

const saveMoneySchema = z.object({
  amount: z.number().min(0.01, 'Amount must be greater than 0'),
  description: z.string().optional()
})

const unsaveMoneySchema = z.object({
  amount: z.number().min(0.01, 'Amount must be greater than 0'),
  description: z.string().optional()
})

type SaveGoalFormData = z.infer<typeof saveGoalSchema>
type SaveMoneyFormData = z.infer<typeof saveMoneySchema>
type UnsaveMoneyFormData = z.infer<typeof unsaveMoneySchema>

export function Savings() {
  const { t } = useTranslation()
  const [activeTab, setActiveTab] = useState<'goal' | 'manage' | 'history'>('manage')
  const [isLoading, setIsLoading] = useState(false)
  const [savingLogs, setSavingLogs] = useState<SavingLog[]>([])
  const [logsLoading, setLogsLoading] = useState(false)
  const { stats, fetchDashboardStats } = useDashboardStore()

  const saveGoalForm = useForm<SaveGoalFormData>({
    resolver: zodResolver(saveGoalSchema),
    defaultValues: {
      targetAmount: 0,
      description: ''
    }
  })

  const saveMoneyForm = useForm<SaveMoneyFormData>({
    resolver: zodResolver(saveMoneySchema),
    defaultValues: {
      amount: 0,
      description: ''
    }
  })

  const unsaveMoneyForm = useForm<UnsaveMoneyFormData>({
    resolver: zodResolver(unsaveMoneySchema),
    defaultValues: {
      amount: 0,
      description: ''
    }
  })

  useEffect(() => {
    if (activeTab === 'history') loadSavingLogs()
  }, [activeTab])

  useEffect(() => {
    if (activeTab === 'goal' && stats?.hasSavingsGoal) {
      saveGoalForm.setValue('targetAmount', stats.savingsGoal || 0)
    }
  }, [activeTab, stats?.hasSavingsGoal, stats?.savingsGoal, saveGoalForm])

  const loadSavingLogs = async () => {
    setLogsLoading(true)
    try {
      const logs = await savingApi.getSavingLogs()
      setSavingLogs(logs)
    } catch (error: any) {
      toast.error(error.message || t('Failed to load saving logs'))
    } finally {
      setLogsLoading(false)
    }
  }

  const onSaveGoalSubmit = async (data: SaveGoalFormData) => {
    setIsLoading(true)
    try {
      const req: SaveGoalRequest = { targetAmount: data.targetAmount, description: data.description || undefined }
      await saveGoalApi.setSaveGoal(req)
      await fetchDashboardStats()
      toast.success(t('Savings goal set successfully!'))
      saveGoalForm.reset()
    } catch (error: any) {
      toast.error(error.message || t('Failed to set savings goal'))
    } finally {
      setIsLoading(false)
    }
  }

  const onSaveSubmit = async (data: SaveMoneyFormData) => {
    const available = (stats?.totalBalance || 0) - (stats?.saved || 0)
    if (data.amount > available) {
      toast.error(t('Amount cannot exceed available balance'))
      return
    }

    setIsLoading(true)
    try {
      const req: SaveMoneyRequest = { amount: data.amount, description: data.description || undefined }
      await savingApi.saveMoney(req)
      await Promise.all([fetchDashboardStats(), loadSavingLogs()])
      toast.success(t('Money saved successfully!'))
      saveMoneyForm.reset()
    } catch (error: any) {
      toast.error(error.message || t('Failed to save money'))
    } finally {
      setIsLoading(false)
    }
  }

  const onUnsaveSubmit = async (data: UnsaveMoneyFormData) => {
    if (data.amount > (stats?.saved || 0)) {
      toast.error(t('Amount cannot exceed saved amount'))
      return
    }

    setIsLoading(true)
    try {
      const req: UnsaveMoneyRequest = { amount: data.amount, description: data.description || undefined }
      await savingApi.unsaveMoney(req)
      await Promise.all([fetchDashboardStats(), loadSavingLogs()])
      toast.success(t('Money unmarked successfully!'))
      unsaveMoneyForm.reset()
    } catch (error: any) {
      toast.error(error.message || t('Failed to unmark money'))
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="p-3 bg-gradient-to-r from-pink-500 to-rose-500 rounded-2xl shadow-lg">
            <PiggyBank className="w-8 h-8 text-white" />
          </div>
          <div>
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white">{t('savings')}</h1>
            <p className="text-gray-600 dark:text-gray-300 mt-1">{t('Manage your savings goals and track your progress')}</p>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-gray-200 dark:border-gray-700">
        {[
          { key: 'manage', icon: DollarSign, label: t('Manage Money') },
          { key: 'goal', icon: Target, label: t('Set Goal') },
          { key: 'history', icon: History, label: t('History') }
        ].map(tab => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key as any)}
            className={`px-6 py-3 text-sm font-medium border-b-2 transition-colors ${
              activeTab === tab.key
                ? 'border-pink-500 text-pink-600 dark:text-pink-400'
                : 'border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300'
            }`}
          >
            <div className="flex items-center gap-2"><tab.icon className="w-4 h-4" />{tab.label}</div>
          </button>
        ))}
      </div>

      {/* Goal Tab */}
      {activeTab === 'goal' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card>
            <CardHeader><CardTitle className="text-lg text-pink-600 dark:text-pink-400">{t('Set Savings Goal')}</CardTitle></CardHeader>
            <CardContent>
              <Form {...saveGoalForm}>
                <form onSubmit={saveGoalForm.handleSubmit(onSaveGoalSubmit)} className="space-y-4">
                  <FormField
                    control={saveGoalForm.control}
                    name="targetAmount"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>{t('Target Amount (AUD)')}</FormLabel>
                        <FormControl>
                          <Input type="number" step="0.01" min="0.01"
                            placeholder={t('Enter target amount')!}
                            {...field}
                            onChange={e => field.onChange(Number.parseFloat(e.target.value))}
                            disabled={isLoading}
                            className="text-lg"
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={saveGoalForm.control}
                    name="description"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>{t('Description (Optional)')}</FormLabel>
                        <FormControl><Textarea placeholder={t('e.g., Emergency fund, Vacation savings...')!} {...field} disabled={isLoading} rows={3} /></FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <Button type="submit" disabled={isLoading} className="w-full bg-gradient-to-r from-pink-500 to-rose-500 text-white">
                    {isLoading ? <Spinner size="sm" /> : <><Target className="w-4 h-4 mr-2" />{t('Set Goal')}</>}
                  </Button>
                </form>
              </Form>
            </CardContent>
          </Card>

          <Card className="bg-gradient-to-r from-pink-50 to-rose-50 dark:from-pink-900/20 dark:to-rose-900/20">
            <CardHeader><CardTitle className="text-lg text-pink-600 dark:text-pink-400">{t('Progress')}</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between"><span>{t('Saved Amount')}</span><span className="text-2xl font-bold text-pink-600">${stats?.saved?.toFixed(2) || '0.00'}</span></div>
              <div className="flex justify-between"><span>{t('Savings Goal')}</span><span>{stats?.hasSavingsGoal ? `$${stats.savingsGoal?.toFixed(0)}` : t('Not set')}</span></div>
              {stats?.hasSavingsGoal && (
                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span>{t('Progress')}</span>
                    <span>{(stats?.savingsProgress ?? 0).toFixed(0)}%</span>
                  </div>
                  <div className="w-full bg-gray-200 dark:bg-gray-700 h-4 rounded-full">
                    <div className="bg-gradient-to-r from-pink-500 to-rose-500 h-4 rounded-full"
                      style={{ width: `${Math.min(stats?.savingsProgress || 0, 100)}%` }} />
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      )}

      {/* Manage Tab */}
      {activeTab === 'manage' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Save Money */}
          <Card>
            <CardHeader className="relative">
              <CardTitle className="text-lg text-emerald-600">{t('Save Money')}</CardTitle>
              <div className="absolute top-4 right-4 text-sm text-gray-600 dark:text-gray-400">
                <div className="text-xs text-gray-500 dark:text-gray-500 mb-1">{t('Available to save')}</div>
                <div className="font-semibold text-emerald-600 dark:text-emerald-400">
                  ${((stats?.totalBalance || 0) - (stats?.saved || 0)).toFixed(2)}
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <Form {...saveMoneyForm}>
                <form onSubmit={saveMoneyForm.handleSubmit(onSaveSubmit)} className="space-y-4">
                  <FormField control={saveMoneyForm.control} name="amount" render={({ field }) => (
                    <FormItem>
                      <FormLabel>{t('Amount (AUD)')}</FormLabel>
                      <FormControl><Input type="number" step="0.01" placeholder={t('Enter amount to save')!}
                        {...field} onChange={e => field.onChange(Number.parseFloat(e.target.value))} disabled={isLoading} /></FormControl>
                      <FormMessage />
                    </FormItem>
                  )}/>
                  <FormField control={saveMoneyForm.control} name="description" render={({ field }) => (
                    <FormItem>
                      <FormLabel>{t('Description (Optional)')}</FormLabel>
                      <FormControl><Textarea placeholder={t('e.g., Emergency fund, Vacation savings...')!} {...field} disabled={isLoading} rows={2} /></FormControl>
                      <FormMessage />
                    </FormItem>
                  )}/>
                  <Button type="submit" disabled={isLoading} className="w-full bg-gradient-to-r from-emerald-500 to-cyan-500 text-white">
                    {isLoading ? <Spinner size="sm" /> : <><Plus className="w-4 h-4 mr-2" />{t('Save Money')}</>}
                  </Button>
                </form>
              </Form>
            </CardContent>
          </Card>

          {/* Unsave Money */}
          <Card>
            <CardHeader className="relative">
              <CardTitle className="text-lg text-pink-600">{t('Unmark as Saved')}</CardTitle>
              <div className="absolute top-4 right-4 text-sm text-gray-600 dark:text-gray-400">
                <div className="text-xs text-gray-500 dark:text-gray-500 mb-1">{t('Available to unmark')}</div>
                <div className="font-semibold text-pink-600 dark:text-pink-400">
                  ${(stats?.saved || 0).toFixed(2)}
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <Form {...unsaveMoneyForm}>
                <form onSubmit={unsaveMoneyForm.handleSubmit(onUnsaveSubmit)} className="space-y-4">
                  <FormField control={unsaveMoneyForm.control} name="amount" render={({ field }) => (
                    <FormItem>
                      <FormLabel>{t('Amount (AUD)')}</FormLabel>
                      <FormControl><Input type="number" step="0.01" placeholder={t('Enter amount to unmark')!}
                        {...field} onChange={e => field.onChange(Number.parseFloat(e.target.value))} disabled={isLoading} /></FormControl>
                      <FormMessage />
                    </FormItem>
                  )}/>
                  <FormField control={unsaveMoneyForm.control} name="description" render={({ field }) => (
                    <FormItem>
                      <FormLabel>{t('Description (Optional)')}</FormLabel>
                      <FormControl><Textarea placeholder={t('e.g., Need for emergency, Change of plans...')!} {...field} disabled={isLoading} rows={2} /></FormControl>
                      <FormMessage />
                    </FormItem>
                  )}/>
                  <Button type="submit" disabled={isLoading} className="w-full bg-gradient-to-r from-pink-500 to-rose-500 text-white">
                    {isLoading ? <Spinner size="sm" /> : <><Minus className="w-4 h-4 mr-2" />{t('Unmark as Saved')}</>}
                  </Button>
                </form>
              </Form>
            </CardContent>
          </Card>
        </div>
      )}

      {/* History */}
      {activeTab === 'history' && (
        <Card>
          <CardHeader><CardTitle>{t('Saving History')}</CardTitle></CardHeader>
          <CardContent>
            {logsLoading ? (
              <div className="text-center py-8"><Spinner size="md" /><p>{t('Loading saving history...')}</p></div>
            ) : (
              savingLogs.length > 0 ? (
                <div className="space-y-3 max-h-96 overflow-y-auto">
                  {savingLogs.map(log => (
                    <div key={log.id} className="flex justify-between p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                      <div><div className="font-medium">{log.actionDisplayName}</div><div className="text-sm">{log.description}</div><div className="text-xs">{format(new Date(log.timestamp),'MMM dd, yyyy HH:mm')}</div></div>
                      <div className="text-right">
                        <div className={`font-bold text-lg ${log.action === 'SAVE' ? 'text-emerald-600' : 'text-pink-600'}`}>
                          {log.action === 'SAVE' ? '+' : '-'}${log.amount.toFixed(2)}
                        </div>
                        <Badge className={log.action === 'SAVE' ? 'bg-emerald-100 text-emerald-700' : 'bg-pink-100 text-pink-700'}>{log.action}</Badge>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-8 text-gray-500"><PiggyBank className="w-12 h-12 mx-auto mb-4" /><p>{t('No saving history found')}</p></div>
              )
            )}
          </CardContent>
        </Card>
      )}
    </div>
  )
}
