import { Link, useLocation } from 'react-router'
import { cn } from '@/lib/utils'
import {
  Home,
  CreditCard,
  Target,
  PiggyBank,
  BarChart3,
  Settings,
  LogOut,
  DollarSign
} from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useAuthStore } from '@/stores/authStore'
import { useTranslation } from 'react-i18next'

export function Sidebar() {
  const location = useLocation()
  const { user, logout } = useAuthStore()
  const { t } = useTranslation()

  const navigation = [
    { name: t('dashboard'), href: '/dashboard', icon: Home },
    { name: t('expenses'), href: '/expenses', icon: CreditCard },
    { name: t('budgets'), href: '/budgets', icon: Target },
    { name: t('savings'), href: '/savings', icon: PiggyBank },
    { name: t('reports'), href: '/reports', icon: BarChart3 },
    { name: t('settings'), href: '/settings', icon: Settings },
  ]

  return (
    <div className="hidden lg:flex h-full w-72 flex-col bg-gradient-to-b from-gray-50 to-white dark:from-gray-900 dark:to-gray-800 border-r border-gray-200/60 dark:border-gray-700/60 backdrop-blur-sm">
      {/* Logo */}
      <div className="flex items-center gap-3 px-6 py-6">
        <div className="relative">
          <div className="flex items-center justify-center w-10 h-10 bg-gradient-to-r from-violet-500 to-purple-500 rounded-2xl shadow-lg shadow-violet-500/25 animate-pulse-glow">
            <DollarSign className="w-6 h-6 text-white" />
          </div>
          <div className="absolute -top-1 -right-1 w-4 h-4 bg-gradient-to-r from-emerald-400 to-cyan-400 rounded-full animate-bounce"></div>
        </div>
        <div>

          <h1 className="text-xl font-bold bg-gradient-to-r from-violet-600 to-purple-600 bg-clip-text text-transparent">
            {t('appName')}
          </h1>
          <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
            {t('appTagline')}
          </p>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-4 py-2 space-y-1">
        {navigation.map((item, index) => {
          const isActive = location.pathname === item.href
          return (
            <Link
              key={item.name}
              to={item.href}
              className={cn(
                'group flex items-center gap-3 px-4 py-3 text-sm font-medium rounded-2xl transition-all duration-300 transform hover:scale-105 relative overflow-hidden',
                isActive
                  ? 'bg-gradient-to-r from-violet-500 to-purple-500 text-white shadow-lg shadow-violet-500/25'
                  : 'text-gray-700 dark:text-gray-300 hover:bg-white dark:hover:bg-gray-700 hover:shadow-md hover:shadow-gray-200/50 dark:hover:shadow-gray-800/50'
              )}
              style={{
                animationDelay: `${index * 50}ms`
              }}
            >
              {isActive && (
                <div className="absolute inset-0 bg-gradient-to-r from-violet-400/20 to-purple-400/20 animate-pulse rounded-2xl"></div>
              )}
              <item.icon
                className={cn(
                  'w-5 h-5 relative z-10 transition-transform duration-300 group-hover:rotate-12',
                  isActive ? 'text-white' : 'text-gray-500 dark:text-gray-400'
                )}
              />
              <span className="relative z-10">{item.name}</span>
              {isActive && (
                <div className="absolute right-3 top-1/2 transform -translate-y-1/2 w-2 h-2 bg-white rounded-full animate-ping"></div>
              )}
            </Link>
          )
        })}
      </nav>

      {/* User info & logout */}
      <div className="p-4 border-t border-gray-200/50 dark:border-gray-700/50">
        <div className="flex items-center gap-3 p-3 mb-3 bg-white/80 dark:bg-gray-700/80 backdrop-blur-sm rounded-2xl shadow-sm border border-gray-100 dark:border-gray-600">
          <div className="relative">
            <div className="w-10 h-10 bg-gradient-to-r from-emerald-400 to-cyan-400 rounded-xl flex items-center justify-center shadow-md">
              <span className="text-sm font-bold text-white">
                {user?.name?.charAt(0).toUpperCase()}
              </span>
            </div>
            <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-green-400 border-2 border-white dark:border-gray-700 rounded-full"></div>
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-semibold text-gray-900 dark:text-white truncate">
              {user?.name}
            </p>
            <p className="text-xs text-gray-500 dark:text-gray-400 truncate">
              {user?.email}
            </p>
          </div>
        </div>
        <Button
          variant="ghost"
          size="sm"
          onClick={logout}
          className="w-full justify-start gap-3 px-4 py-3 text-gray-600 dark:text-gray-400 hover:bg-red-50 dark:hover:bg-red-900/20 hover:text-red-600 dark:hover:text-red-400 rounded-2xl transition-all duration-300 group"
        >
          <LogOut className="w-4 h-4 group-hover:rotate-12 transition-transform duration-300" />
          {t('logout')}
        </Button>
      </div>
    </div>
  )
}
