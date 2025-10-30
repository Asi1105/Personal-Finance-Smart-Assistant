import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { api } from '@/lib/axios'
import toast from 'react-hot-toast'

interface Expense {
  id: string
  amount: number
  category: string
  description: string
  date: string
  userId: string
}

interface CreateExpenseDto {
  amount: number
  category: string
  description: string
  date: string
}

export const useExpenses = () => {
  return useQuery<Expense[]>({
    queryKey: ['expenses'],
    queryFn: async () => {
      const { data } = await api.get('/expenses')
      return data
    },
  })
}

export const useCreateExpense = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: async (expense: CreateExpenseDto) => {
      const { data } = await api.post('/expenses', expense)
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['expenses'] })
      toast.success('Expense added successfully!')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to add expense')
    },
  })
}

export const useDeleteExpense = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/expenses/${id}`)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['expenses'] })
      toast.success('Expense deleted successfully!')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to delete expense')
    },
  })
}