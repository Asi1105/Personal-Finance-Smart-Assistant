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
  DollarSign,
  X
} from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useAuthStore } from '@/stores/authStore'
import { useMobileMenu } from '@/contexts/MobileMenuContext'
import { useEffect } from 'react'
import { useTranslation } from 'react-i18next'

const navigation = [
  { name: 'Dashboard', href: '/dashboard', icon: Home },
  { name: 'Expenses', href: '/expenses', icon: CreditCard },
  { name: 'Budgets', href: '/budgets', icon: Target },
  { name: 'Savings', href: '/savings', icon: PiggyBank },
  { name: 'Reports', href: '/reports', icon: BarChart3 },
  { name: 'Settings', href: '/settings', icon: Settings },
]

export function MobileSidebar() {
  const location = useLocation()
  const { user, logout } = useAuthStore()
  const { isMobileMenuOpen, closeMobileMenu } = useMobileMenu()
  const { t } = useTranslation() //

  // Close menu when route changes
  useEffect(() => {
    closeMobileMenu()
  }, [location.pathname, closeMobileMenu])

  // Prevent scroll when menu is open
  useEffect(() => {
    if (isMobileMenuOpen) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = 'unset'
    }
    return () => {
      document.body.style.overflow = 'unset'
    }
  }, [isMobileMenuOpen])

  if (!isMobileMenuOpen) return null

  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 z-40 bg-black/50 backdrop-blur-sm backdrop-fade-in lg:hidden"
        onClick={closeMobileMenu}
      />

      {/* Mobile Sidebar */}
      <div className="fixed top-0 right-0 z-50 h-full w-80 max-w-[85vw] bg-white dark:bg-gray-900 shadow-2xl slide-in-right lg:hidden">
        <div className="flex h-full flex-col">
          {/* Header */}
          <div className="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
            <div className="flex items-center gap-3">
              <div className="relative">
                <div className="flex items-center justify-center w-10 h-10 bg-gradient-to-r from-violet-500 to-purple-500 rounded-2xl shadow-lg shadow-violet-500/25">
                  <DollarSign className="w-6 h-6 text-white" />
                </div>
                <div className="absolute -top-1 -right-1 w-4 h-4 bg-gradient-to-r from-emerald-400 to-cyan-400 rounded-full animate-bounce"></div>
              </div>
              <div>

                <h1 className="text-lg font-bold bg-gradient-to-r from-violet-600 to-purple-600 bg-clip-text text-transparent">
                  {t('appName')}
                </h1>
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  {t('appTagline')}
                </p>
              </div>
            </div>
            <Button
              variant="ghost"
              size="sm"
              onClick={closeMobileMenu}
              className="p-2 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-xl"
            >
              <X className="w-5 h-5" />
            </Button>
          </div>

          {/* Navigation */}
          <nav className="flex-1 p-4 space-y-2 overflow-y-auto">
            {navigation.map((item, index) => {
              const isActive = location.pathname === item.href
              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={cn(
                    'group flex items-center gap-3 px-4 py-4 text-sm font-medium rounded-2xl transition-all duration-300 relative overflow-hidden',
                    isActive
                      ? 'bg-gradient-to-r from-violet-500 to-purple-500 text-white shadow-lg shadow-violet-500/25'
                      : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800'
                  )}
                  style={{
                    animationDelay: `${index * 50}ms`
                  }}
                >
                  {isActive && (
                    <div className="absolute inset-0 bg-gradient-to-r from-violet-400/20 to-purple-400/20 animate-pulse rounded-2xl"></div>
                  )}
                  <item.icon className={cn(
                    'w-5 h-5 relative z-10 transition-transform duration-300 group-hover:rotate-12',
                    isActive ? 'text-white' : 'text-gray-500 dark:text-gray-400'
                  )} />
                  <span className="relative z-10">{t(item.name.toLowerCase())}</span>
                  {isActive && (
                    <div className="absolute right-3 top-1/2 transform -translate-y-1/2 w-2 h-2 bg-white rounded-full animate-ping"></div>
                  )}
                </Link>
              )
            })}
          </nav>

          {/* User info & logout */}
          <div className="p-4 border-t border-gray-200 dark:border-gray-700">
            <div className="flex items-center gap-3 p-3 mb-3 bg-gray-50 dark:bg-gray-800 rounded-2xl">
              <div className="relative">
                <div className="w-10 h-10 bg-gradient-to-r from-emerald-400 to-cyan-400 rounded-xl flex items-center justify-center shadow-md">
                  <span className="text-sm font-bold text-white">
                    {user?.name.charAt(0).toUpperCase()}
                  </span>
                </div>
                <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-green-400 border-2 border-white dark:border-gray-800 rounded-full"></div>
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
      </div>
    </>
  )
}
