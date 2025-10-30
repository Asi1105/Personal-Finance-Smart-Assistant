export interface User {
  id: string
  name: string
  email: string
  createdAt: string
}

// Re-export authentication related types
export type {
  UserDto,
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  ApiResponse,
  ErrorResponse
} from './auth'

export interface Expense {
  id: string
  amount: number
  category: string
  description: string
  date: string
  userId: string
  createdAt: string
  updatedAt: string
}

export interface Budget {
  id: string
  category: string
  limit: number
  spent: number
  userId: string
  createdAt: string
  updatedAt: string
}

export interface Category {
  id: string
  name: string
  icon: string
  color: string
}

export interface MonthlyData {
  month: string
  income: number
  expenses: number
  savings: number
}

export interface CategoryExpense {
  category: string
  amount: number
  percentage: number
  count: number
}

// Account related types
export interface Account {
  id: string
  balance: number
  saved: number
  userId: string
}

// Transaction related types
export const TransactionType = {
  IN: 'IN',
  OUT: 'OUT'
} as const

export type TransactionType = typeof TransactionType[keyof typeof TransactionType]

export const ExpenseCategory = {
  FOOD_DINING: 'FOOD_DINING',
  TRANSPORTATION: 'TRANSPORTATION',
  ENTERTAINMENT: 'ENTERTAINMENT',
  SHOPPING: 'SHOPPING',
  BILLS_UTILITIES: 'BILLS_UTILITIES',
  HEALTHCARE: 'HEALTHCARE',
  TRAVEL: 'TRAVEL',
  EDUCATION: 'EDUCATION',
  OTHER: 'OTHER'
} as const

export type ExpenseCategory = typeof ExpenseCategory[keyof typeof ExpenseCategory]

export interface Transaction {
  id: string
  type: TransactionType
  date: string
  expenseCategory?: ExpenseCategory
  detail: string
  amount: number
  note: string
  accountId: string
}

// Save Goal related types
export interface SaveGoal {
  id: string
  targetAmount: number
  description: string
  dueDate: string
  userId: string
}

// Saving Log related types
export const SavingAction = {
  SAVE: 'SAVE',
  UNSAVE: 'UNSAVE'
} as const

export type SavingAction = typeof SavingAction[keyof typeof SavingAction]

export interface SavingLog {
  id: string
  action: SavingAction
  amount: number
  description: string
  timestamp: string
  userId: string
  accountId: string
}