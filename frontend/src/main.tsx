import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { QueryClientProvider } from '@tanstack/react-query'
import { Toaster } from 'react-hot-toast'
import { createBrowserRouter, Navigate } from "react-router";
import { RouterProvider } from "react-router/dom";
import './App.css'
import { queryClient } from '@/lib/queryClient'
import { ProtectedRoute } from '@/components/ProtectedRoute'
import { AppLayout } from '@/components/Layout/AppLayout'
import { Login } from '@/pages/Login'
import { Register } from '@/pages/Register'
import { Dashboard } from '@/pages/Dashboard'
import { Expenses } from '@/pages/Expenses'
import { AddExpense } from '@/pages/AddExpense'
import { EditExpense } from '@/pages/EditExpense'
import { Budgets } from '@/pages/Budgets'
import { AddBudget } from '@/pages/AddBudget'
import { EditBudget } from '@/pages/EditBudget'
import { Savings } from '@/pages/Savings'
import { Reports } from '@/pages/Reports'
import { Settings } from '@/pages/Settings'
import { ThemeProvider } from '@/contexts/ThemeContext'
import { MobileMenuProvider } from '@/contexts/MobileMenuContext'
import { AuthProvider } from '@/components/AuthProvider'
import './i18n'

const router = createBrowserRouter([
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/register", 
    element: <Register />,
  },
  {
    path: "/",
    element: (
      <ProtectedRoute>
        <MobileMenuProvider>
          <AppLayout />
        </MobileMenuProvider>
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/dashboard" replace />,
      },
      {
        path: "dashboard",
        element: <Dashboard />,
      },
      {
        path: "expenses",
        element: <Expenses />,
      },
      {
        path: "expenses/add",
        element: <AddExpense />,
      },
      {
        path: "expenses/:id/edit",
        element: <EditExpense />,
      },
      {
        path: "budgets",
        element: <Budgets />,
      },
      {
        path: "budgets/add",
        element: <AddBudget />,
      },
      {
        path: "budgets/:id/edit",
        element: <EditBudget />,
      },
      {
        path: "savings",
        element: <Savings />,
      },
      {
        path: "reports",
        element: <Reports />,
      },
      {
        path: "settings",
        element: <Settings />,
      },
    ],
  },
]);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <RouterProvider router={router} />
          <Toaster 
            position="top-right"
            toastOptions={{
              duration: 3000,
              className: 'dark:bg-gray-800 dark:text-white',
              style: {
                background: 'var(--toast-bg, #333)',
                color: 'var(--toast-color, #fff)',
              },
            }}
          />
        </AuthProvider>
      </QueryClientProvider>
    </ThemeProvider>
  </StrictMode>,
)
