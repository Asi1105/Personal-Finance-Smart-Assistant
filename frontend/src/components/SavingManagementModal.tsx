import { useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { format } from 'date-fns'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Spinner } from '@/components/ui/loading'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
// Removed tabs import - using simple state-based switching instead
import { savingApi, type SaveMoneyRequest, type UnsaveMoneyRequest, type SavingLog } from '@/services/savingService'
import { useDashboardStore } from '@/stores/dashboardStore'
import toast from 'react-hot-toast'

const saveMoneySchema = z.object({
  amount: z.number().min(0.01, 'Amount must be greater than 0'),
  description: z.string().optional()
})

const unsaveMoneySchema = z.object({
  amount: z.number().min(0.01, 'Amount must be greater than 0'),
  description: z.string().optional()
})

type SaveMoneyFormData = z.infer<typeof saveMoneySchema>
type UnsaveMoneyFormData = z.infer<typeof unsaveMoneySchema>

interface SavingManagementModalProps {
  readonly isOpen: boolean
  readonly onClose: () => void
  readonly availableBalance: number
  readonly savedAmount: number
}

export function SavingManagementModal({ isOpen, onClose, availableBalance, savedAmount }: SavingManagementModalProps) {
  const [isLoading, setIsLoading] = useState(false)
  const [savingLogs, setSavingLogs] = useState<SavingLog[]>([])
  const [logsLoading, setLogsLoading] = useState(false)
  const [activeTab, setActiveTab] = useState<'manage' | 'history'>('manage')
  const { fetchDashboardStats } = useDashboardStore()

  const saveForm = useForm<SaveMoneyFormData>({
    resolver: zodResolver(saveMoneySchema),
    defaultValues: {
      amount: 0,
      description: ''
    }
  })

  const unsaveForm = useForm<UnsaveMoneyFormData>({
    resolver: zodResolver(unsaveMoneySchema),
    defaultValues: {
      amount: 0,
      description: ''
    }
  })

  // Load saving logs when modal opens
  useEffect(() => {
    if (isOpen) {
      loadSavingLogs()
    }
  }, [isOpen])

  const loadSavingLogs = async () => {
    setLogsLoading(true)
    try {
      const logs = await savingApi.getSavingLogs()
      setSavingLogs(logs)
    } catch (error: any) {
      toast.error(error.message || 'Failed to load saving logs')
    } finally {
      setLogsLoading(false)
    }
  }

  const onSaveSubmit = async (data: SaveMoneyFormData) => {
    if (data.amount > availableBalance) {
      toast.error('Amount cannot exceed available balance')
      return
    }

    setIsLoading(true)
    
    try {
      const saveMoneyRequest: SaveMoneyRequest = {
        amount: data.amount,
        description: data.description || undefined
      }
      
      await savingApi.saveMoney(saveMoneyRequest)
      
      // Refresh dashboard data and logs
      await Promise.all([
        fetchDashboardStats(),
        loadSavingLogs()
      ])
      
      toast.success('Money saved successfully!')
      saveForm.reset()
    } catch (error: any) {
      toast.error(error.message || 'Failed to save money')
    } finally {
      setIsLoading(false)
    }
  }

  const onUnsaveSubmit = async (data: UnsaveMoneyFormData) => {
    if (data.amount > savedAmount) {
      toast.error('Amount cannot exceed saved amount')
      return
    }

    setIsLoading(true)
    
    try {
      const unsaveMoneyRequest: UnsaveMoneyRequest = {
        amount: data.amount,
        description: data.description || undefined
      }
      
      await savingApi.unsaveMoney(unsaveMoneyRequest)
      
      // Refresh dashboard data and logs
      await Promise.all([
        fetchDashboardStats(),
        loadSavingLogs()
      ])
      
      toast.success('Money unmarked successfully!')
      unsaveForm.reset()
    } catch (error: any) {
      toast.error(error.message || 'Failed to unmark money')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-2xl max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-xl font-bold bg-gradient-to-r from-pink-600 to-rose-600 bg-clip-text text-transparent">
            Savings Management 💰
          </DialogTitle>
        </DialogHeader>

        {/* Simple tab navigation */}
        <div className="flex border-b border-gray-200 dark:border-gray-700 mb-6">
          <button
            onClick={() => setActiveTab('manage')}
            className={`px-4 py-2 text-sm font-medium border-b-2 transition-colors ${
              activeTab === 'manage'
                ? 'border-pink-500 text-pink-600 dark:text-pink-400'
                : 'border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300'
            }`}
          >
            Manage Savings
          </button>
          <button
            onClick={() => setActiveTab('history')}
            className={`px-4 py-2 text-sm font-medium border-b-2 transition-colors ${
              activeTab === 'history'
                ? 'border-pink-500 text-pink-600 dark:text-pink-400'
                : 'border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300'
            }`}
          >
            History
          </button>
        </div>

        {activeTab === 'manage' && (
          <div className="space-y-6">
            {/* Balance Overview */}
            <div className="grid grid-cols-2 gap-4">
              <Card className="bg-gradient-to-r from-emerald-50 to-cyan-50 dark:from-emerald-900/20 dark:to-cyan-900/20 border border-emerald-200/50 dark:border-emerald-700/50">
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-emerald-600 dark:text-emerald-400">Available to Save</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-emerald-700 dark:text-emerald-300">
                    ${availableBalance.toFixed(2)}
                  </div>
                </CardContent>
              </Card>

              <Card className="bg-gradient-to-r from-pink-50 to-rose-50 dark:from-pink-900/20 dark:to-rose-900/20 border border-pink-200/50 dark:border-pink-700/50">
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm text-pink-600 dark:text-pink-400">Currently Saved</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-pink-700 dark:text-pink-300">
                    ${savedAmount.toFixed(2)}
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Save Money Form */}
            <Card>
              <CardHeader>
                <CardTitle className="text-lg text-emerald-600 dark:text-emerald-400">Save Money</CardTitle>
              </CardHeader>
              <CardContent>
                <form onSubmit={saveForm.handleSubmit(onSaveSubmit)} className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="save-amount" className="text-sm font-medium">
                      Amount (AUD)
                    </Label>
                    <Input
                      id="save-amount"
                      type="number"
                      step="0.01"
                      min="0.01"
                      max={availableBalance}
                      placeholder="Enter amount to save"
                      {...saveForm.register('amount', { valueAsNumber: true })}
                      disabled={isLoading}
                      className="text-lg [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none [-moz-appearance:textfield]"
                    />
                    {saveForm.formState.errors.amount && (
                      <p className="text-sm text-red-500">
                        {saveForm.formState.errors.amount.message}
                      </p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="save-description" className="text-sm font-medium">
                      Description (Optional)
                    </Label>
                    <Textarea
                      id="save-description"
                      placeholder="e.g., Emergency fund, Vacation savings..."
                      {...saveForm.register('description')}
                      disabled={isLoading}
                      rows={2}
                    />
                  </div>

                  <Button
                    type="submit"
                    disabled={isLoading || availableBalance <= 0}
                    className="w-full bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-600 hover:to-cyan-600 text-white"
                  >
                    {isLoading ? (
                      <div className="flex items-center gap-2">
                        <Spinner size="sm" className="border-white/30 border-t-white" />
                        Saving...
                      </div>
                    ) : (
                      'Save Money 💰'
                    )}
                  </Button>
                </form>
              </CardContent>
            </Card>

            {/* Unsave Money Form */}
            <Card>
              <CardHeader>
                <CardTitle className="text-lg text-pink-600 dark:text-pink-400">Unmark as Saved</CardTitle>
              </CardHeader>
              <CardContent>
                <form onSubmit={unsaveForm.handleSubmit(onUnsaveSubmit)} className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="unsave-amount" className="text-sm font-medium">
                      Amount (AUD)
                    </Label>
                    <Input
                      id="unsave-amount"
                      type="number"
                      step="0.01"
                      min="0.01"
                      max={savedAmount}
                      placeholder="Enter amount to unmark"
                      {...unsaveForm.register('amount', { valueAsNumber: true })}
                      disabled={isLoading}
                      className="text-lg [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none [-moz-appearance:textfield]"
                    />
                    {unsaveForm.formState.errors.amount && (
                      <p className="text-sm text-red-500">
                        {unsaveForm.formState.errors.amount.message}
                      </p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="unsave-description" className="text-sm font-medium">
                      Description (Optional)
                    </Label>
                    <Textarea
                      id="unsave-description"
                      placeholder="e.g., Need for emergency, Change of plans..."
                      {...unsaveForm.register('description')}
                      disabled={isLoading}
                      rows={2}
                    />
                  </div>

                  <Button
                    type="submit"
                    disabled={isLoading || savedAmount <= 0}
                    className="w-full bg-gradient-to-r from-pink-500 to-rose-500 hover:from-pink-600 hover:to-rose-600 text-white"
                  >
                    {isLoading ? (
                      <div className="flex items-center gap-2">
                        <Spinner size="sm" className="border-white/30 border-t-white" />
                        Unmarking...
                      </div>
                    ) : (
                      'Unmark as Saved 💸'
                    )}
                  </Button>
                </form>
              </CardContent>
            </Card>
          </div>
        )}

        {activeTab === 'history' && (
          <div className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Saving History</CardTitle>
              </CardHeader>
              <CardContent>
                {(() => {
                  if (logsLoading) {
                    return (
                      <div className="text-center py-8">
                        <Spinner size="md" />
                        <p className="text-sm text-gray-500 mt-2">Loading saving history...</p>
                      </div>
                    )
                  }
                  
                  if (savingLogs.length > 0) {
                    return (
                      <div className="space-y-3 max-h-60 overflow-y-auto">
                        {savingLogs.map((log) => (
                          <div key={log.id} className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
                            <div className="flex items-center gap-3">
                              <div className="text-2xl">{log.icon}</div>
                              <div>
                                <div className="font-medium text-gray-900 dark:text-white">
                                  {log.actionDisplayName}
                                </div>
                                <div className="text-sm text-gray-500 dark:text-gray-400">
                                  {log.description}
                                </div>
                                <div className="text-xs text-gray-400 dark:text-gray-500">
                                  {format(new Date(log.timestamp), 'MMM dd, yyyy HH:mm')}
                                </div>
                              </div>
                            </div>
                            <div className="text-right">
                              <div className={`font-bold ${log.action === 'SAVE' ? 'text-emerald-600' : 'text-pink-600'}`}>
                                {log.action === 'SAVE' ? '+' : '-'}${log.amount.toFixed(2)}
                              </div>
                              <Badge 
                                variant="secondary" 
                                className={`text-xs ${log.action === 'SAVE' ? 'bg-emerald-100 text-emerald-700' : 'bg-pink-100 text-pink-700'}`}
                              >
                                {log.action}
                              </Badge>
                            </div>
                          </div>
                        ))}
                      </div>
                    )
                  }
                  
                  return (
                    <div className="text-center py-8 text-gray-500 dark:text-gray-400">
                      <p>No saving history found</p>
                    </div>
                  )
                })()}
              </CardContent>
            </Card>
          </div>
        )}

        <div className="flex justify-end pt-4">
          <Button variant="outline" onClick={onClose} disabled={isLoading}>
            Close
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}
