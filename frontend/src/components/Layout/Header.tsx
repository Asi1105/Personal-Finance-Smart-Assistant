import { Menu, Sun, Moon, Plus, Globe } from 'lucide-react'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { useMobileMenu } from '@/contexts/MobileMenuContext'
import { useTheme } from '@/contexts/ThemeContext'
import { DepositModal } from '@/components/DepositModal'
import { useTranslation } from 'react-i18next'

export function Header() {
  const { toggleMobileMenu } = useMobileMenu()
  const { theme, toggleTheme } = useTheme()
  const [isDepositModalOpen, setIsDepositModalOpen] = useState(false)
  const { t, i18n } = useTranslation()

  // Language switching logic
  const toggleLanguage = () => {
    const newLang = i18n.language === 'zh' ? 'en' : 'zh'
    i18n.changeLanguage(newLang)
    localStorage.setItem('lang', newLang)
  }

  return (
    <header className="bg-white/80 dark:bg-gray-900/80 backdrop-blur-xl border-b border-gray-200/50 dark:border-gray-700/50 px-4 lg:px-6 py-4 sticky top-0 z-10">
      <div className="flex items-center justify-between">
        {/* Left side - Mobile menu */}
        <div className="flex items-center gap-3">
          <Button
            variant="ghost"
            size="sm"
            onClick={toggleMobileMenu}
            className="lg:hidden p-2 text-gray-600 dark:text-gray-400 hover:text-violet-600 dark:hover:text-violet-400 hover:bg-violet-50 dark:hover:bg-violet-900/20 rounded-xl transition-all duration-200"
          >
            <Menu className="w-5 h-5" />
          </Button>
        </div>

        {/* Right side */}
        <div className="flex items-center gap-2 lg:gap-3">

          {/* Deposit Button */}
          <Button
            variant="default"
            size="sm"
            onClick={() => setIsDepositModalOpen(true)}
            className="bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-600 hover:to-cyan-600 text-white px-4 py-2 rounded-xl shadow-lg shadow-emerald-500/25 hover:shadow-emerald-500/40 transition-all duration-300 transform hover:scale-105"
          >
            <Plus className="w-4 h-4 mr-2" />
            <span className="hidden sm:inline">{t('deposit')}</span>
          </Button>

          {/* Language Switcher */}
          <Button
            variant="ghost"
            size="sm"
            onClick={toggleLanguage}
            title={i18n.language === 'zh' ? 'Switch to English' : 'Switch to Chinese'}
            className="p-2 text-gray-600 dark:text-gray-400 hover:text-violet-600 dark:hover:text-violet-400 hover:bg-violet-50 dark:hover:bg-violet-900/20 rounded-xl transition-all duration-200 group"
          >
            <Globe className="w-5 h-5 group-hover:rotate-12 transition-transform duration-300" />
            <span className="ml-2 text-sm font-medium hidden sm:inline">
              {i18n.language === 'zh' ? 'CN' : 'EN'}
            </span>
          </Button>

          {/* Theme Toggle */}
          <Button
            variant="ghost"
            size="sm"
            onClick={toggleTheme}
            className="p-2 text-gray-600 dark:text-gray-400 hover:text-violet-600 dark:hover:text-violet-400 hover:bg-violet-50 dark:hover:bg-violet-900/20 rounded-xl transition-all duration-200 group"
          >
            {theme === 'dark' ? (
              <Sun className="w-5 h-5 group-hover:rotate-12 transition-transform duration-300" />
            ) : (
              <Moon className="w-5 h-5 group-hover:rotate-12 transition-transform duration-300" />
            )}
          </Button>

        </div>
      </div>

      {/* Deposit Modal */}
      <DepositModal
        isOpen={isDepositModalOpen}
        onClose={() => setIsDepositModalOpen(false)}
      />
    </header>
  )
}
