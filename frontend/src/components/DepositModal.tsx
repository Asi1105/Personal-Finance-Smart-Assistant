import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Spinner } from '@/components/ui/loading'
import { depositApi, type DepositRequest } from '@/services/depositService'
import { useDashboardStore } from '@/stores/dashboardStore'
import toast from 'react-hot-toast'

const depositSchema = z.object({
  amount: z.number().min(0.01, 'Amount must be greater than 0'),
  description: z.string().optional()
})

type DepositFormData = z.infer<typeof depositSchema>

interface DepositModalProps {
  readonly isOpen: boolean
  readonly onClose: () => void
}

export function DepositModal({ isOpen, onClose }: DepositModalProps) {
  const [isLoading, setIsLoading] = useState(false)
  const { fetchDashboardStats, fetchRecentTransactions } = useDashboardStore()

  const form = useForm<DepositFormData>({
    resolver: zodResolver(depositSchema),
    defaultValues: {
      amount: 0,
      description: ''
    }
  })

  const onSubmit = async (data: DepositFormData) => {
    setIsLoading(true)
    
    try {
      const depositRequest: DepositRequest = {
        amount: data.amount,
        description: data.description || undefined
      }
      
      await depositApi.processDeposit(depositRequest)
      
      // Refresh dashboard data and recent transactions
      await Promise.all([
        fetchDashboardStats(),
        fetchRecentTransactions()
      ])
      
      toast.success('Deposit processed successfully!')
      form.reset()
      onClose()
    } catch (error: any) {
      toast.error(error.message || 'Failed to process deposit')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="text-xl font-bold bg-gradient-to-r from-emerald-600 to-cyan-600 bg-clip-text text-transparent">
            Make a Deposit 💰
          </DialogTitle>
        </DialogHeader>
        
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="amount" className="text-sm font-medium">
              Amount ($)
            </Label>
            <Input
              id="amount"
              type="number"
              step="0.01"
              min="0.01"
              placeholder="Enter amount"
              {...form.register('amount', { valueAsNumber: true })}
              disabled={isLoading}
              className="text-lg [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none [-moz-appearance:textfield]"
            />
            {form.formState.errors.amount && (
              <p className="text-sm text-red-500">
                {form.formState.errors.amount.message}
              </p>
            )}
          </div>


          <div className="space-y-2">
            <Label htmlFor="description" className="text-sm font-medium">
              Description (Optional)
            </Label>
            <Textarea
              id="description"
              placeholder="e.g., Salary deposit, Gift money..."
              {...form.register('description')}
              disabled={isLoading}
              rows={3}
            />
            {form.formState.errors.description && (
              <p className="text-sm text-red-500">
                {form.formState.errors.description.message}
              </p>
            )}
          </div>

          <div className="flex gap-3 pt-4">
            <Button
              type="button"
              variant="outline"
              onClick={onClose}
              disabled={isLoading}
              className="flex-1"
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={isLoading}
              className="flex-1 bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-600 hover:to-cyan-600 text-white"
            >
              {isLoading ? (
                <div className="flex items-center gap-2">
                  <Spinner size="sm" className="border-white/30 border-t-white" />
                  Processing...
                </div>
              ) : (
                'Deposit 💰'
              )}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
}
