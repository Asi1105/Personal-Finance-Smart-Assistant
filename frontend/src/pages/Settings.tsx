import { useState, useEffect } from 'react'
import {
  User,
  Palette,
  Shield,
  Lock,
  Save,
  Trash2,
  Database
} from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

import { useTheme } from '@/contexts/ThemeContext'
import { useTranslation } from 'react-i18next'
import toast from 'react-hot-toast'
import { useAuthStore } from '@/stores/authStore'

const getAuthHeaders = (): Record<string, string> => {
  const token = localStorage.getItem('token')
  if (!token) return {}
  return { Authorization: `Bearer ${token}` }
}

export function Settings() {
  const { t, i18n } = useTranslation()
  const { theme, toggleTheme } = useTheme()
  const { refreshUser } = useAuthStore()

  // User information
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  })

  const [isSaving, setIsSaving] = useState(false)


  // Preferences (language, currency)
  const [preferences, setPreferences] = useState({
    language: i18n.language || localStorage.getItem('language') || 'en',
    currency: 'AUD',
  })

  // Load user information from API when page loads
  useEffect(() => {
    const loadUserInfo = async () => {
      try {
        const token = localStorage.getItem('token')
        if (token) {
          const response = await fetch('http://localhost:8080/auth/me', {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          })
          if (response.ok) {
            const result = await response.json()
            if (result.success && result.data) {
              const newFormData = {
                name: result.data.name || '',
                email: result.data.email || '',
                currentPassword: '',
                newPassword: '',
                confirmPassword: '',
              }
              setFormData(newFormData)
            }
          }
        } else {
          // Fallback to localStorage if no token
          const fallbackData = {
            name: localStorage.getItem('userName') || '',
            email: localStorage.getItem('email') || '',
            currentPassword: '',
            newPassword: '',
            confirmPassword: '',
          }
          setFormData(fallbackData)
        }
      } catch (error) {
        console.error('Failed to load user info:', error)
        // Fallback to localStorage if API fails
        const fallbackData = {
          name: localStorage.getItem('userName') || '',
          email: localStorage.getItem('email') || '',
          currentPassword: '',
          newPassword: '',
          confirmPassword: '',
        }
        setFormData(fallbackData)
      }
    }

    loadUserInfo()
  }, [])

  // Initialize language from localStorage on page load
  useEffect(() => {
    const savedLanguage = localStorage.getItem('language')
    if (savedLanguage && savedLanguage !== i18n.language) {
      i18n.changeLanguage(savedLanguage)
    }
  }, [i18n])

  // Automatically monitor global language changes (ensure synchronization with top-right corner)
  useEffect(() => {
    setPreferences(prev => ({
      ...prev,
      language: i18n.language,
    }))
  }, [i18n.language])

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }))
  }

  const handlePreferenceChange = (field: string, value: string) => {
    setPreferences(prev => ({ ...prev, [field]: value }))
  }

  // Change language
  const handleLanguageChange = (value: string) => {
    handlePreferenceChange('language', value)
    i18n.changeLanguage(value)
    localStorage.setItem('language', value)
    // Note: Backend preferences will be saved when user clicks "Save all settings"
  }

  // Update username
  const updateUserName = async () => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/settings/update-name?newName=${encodeURIComponent(formData.name)}`,
        { method: 'PUT', headers: getAuthHeaders() }
      )
      const result = await res.json()
      if (result.success || result.includes?.("successfully")) {
        localStorage.setItem('userName', formData.name)
        return true
      }
      return false
    } catch {
      return false
    }
  }

  // Update email
  const updateEmail = async () => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/settings/update-email?newEmail=${encodeURIComponent(formData.email)}`,
        { method: 'PUT', headers: getAuthHeaders() }
      )
      const result = await res.json()
      if (result.success || result.includes?.("successfully")) {
        localStorage.setItem('email', formData.email)
        return true
      }
      return false
    } catch {
      return false
    }
  }

  // Update password
  const updatePassword = async () => {
    // Check if any password field has content
    const hasPasswordFields = formData.currentPassword || formData.newPassword || formData.confirmPassword

    if (!hasPasswordFields) {
      return 'NO_CHANGE'
    }

    // If any password field is filled, all must be filled
    if (!formData.currentPassword || !formData.newPassword || !formData.confirmPassword) {
      toast.error(t('Please fill in all password fields'), {
        duration: 4000,
        icon: '⚠️',
      })
      return 'FAILED'
    }

    if (formData.newPassword !== formData.confirmPassword) {
      toast.error(t('New passwords do not match'), {
        duration: 4000,
        icon: '⚠️',
      })
      return 'FAILED'
    }

    const strongPattern = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,}$/
    if (!strongPattern.test(formData.newPassword)) {
      toast.error(t('Password must include upper, lower, number, and special character'), {
        duration: 4000,
        icon: '⚠️',
      })
      return 'FAILED'
    }

    // Check if new password is the same as current password
    if (formData.currentPassword === formData.newPassword) {
      toast.error(t('New password cannot be the same as the current password'), {
        duration: 4000,
        icon: '⚠️',
      })
      return 'FAILED'
    }

    try {
      const res = await fetch(
        `http://localhost:8080/api/settings/update-password?currentPassword=${encodeURIComponent(formData.currentPassword)}&newPassword=${encodeURIComponent(formData.newPassword)}`,
        { method: 'PUT', headers: getAuthHeaders() }
      )
      const result = await res.json()
      if (result.success || result.includes?.("successfully")) return 'SUCCESS'

      // Handle specific error messages
      const errorMessage = result.error?.message || result.message || t('Failed to update password')
      if (errorMessage.includes('Current password is incorrect')) {
        toast.error(t('Current password is incorrect'), {
          duration: 4000,
          icon: '⚠️',
        })
      } else if (errorMessage.includes('Password must include')) {
        toast.error(t('Password must include upper, lower, number, and special character'), {
          duration: 4000,
          icon: '⚠️',
        })
      } else if (errorMessage.includes('New password cannot be the same')) {
        toast.error(t('New password cannot be the same as the old password'), {
          duration: 4000,
          icon: '⚠️',
        })
      } else {
        toast.error(errorMessage, {
          duration: 4000,
          icon: '❌',
        })
      }
      return 'FAILED'
    } catch {
      toast.error(t('Failed to update password'), {
        duration: 4000,
        icon: '❌',
      })
      return 'FAILED'
    }
  }

  // Update preferences (language / theme)
  const updatePreferences = async () => {
    try {
      const res = await fetch(`http://localhost:8080/api/settings/preferences`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', ...getAuthHeaders() },
        body: JSON.stringify({
          language: preferences.language,
          currency: preferences.currency,
          theme: theme,
        }),
      })
      return res.ok
    } catch {
      return false
    }
  }

  // Save all settings with one click
  const handleSaveAll = async () => {
    if (isSaving) return // Prevent multiple clicks

    setIsSaving(true)

    // Safety timeout to ensure isSaving is reset even if something goes wrong
    const safetyTimeout = setTimeout(() => {
      setIsSaving(false)
    }, 10000) // 10 seconds timeout

    let passwordUpdated = false
    let nameUpdated = false
    let emailUpdated = false

    // First, check if there are any changes to make
    const hasPasswordFields = formData.currentPassword || formData.newPassword || formData.confirmPassword
    const currentStoredName = localStorage.getItem('userName') || ''
    const nameChanged = formData.name && formData.name.trim() !== currentStoredName.trim()
    const currentStoredEmail = localStorage.getItem('email') || ''
    const emailChanged = formData.email && formData.email.trim() !== currentStoredEmail.trim()
    const currentLanguage = localStorage.getItem('language') || 'en'
    const prefChanged = preferences.language !== currentLanguage || preferences.currency !== 'AUD'

    // If no changes detected, show info message and return
    if (!hasPasswordFields && !nameChanged && !emailChanged && !prefChanged) {
      clearTimeout(safetyTimeout)
      toast(t('No changes detected.'), {
        duration: 3000,
        icon: 'ℹ️',
      })
      setIsSaving(false)
      return
    }

    try {
      // Check and update password if any password field is filled
      if (hasPasswordFields) {
        const passwordResult = await updatePassword()
        if (passwordResult === 'FAILED') {
          clearTimeout(safetyTimeout)
          setIsSaving(false)
          return
        }
        if (passwordResult === 'SUCCESS') {
          passwordUpdated = true
        }
        // NO_CHANGE is handled by the early return check above
      }

      // Check and update username if changed
      if (nameChanged) {
        const nameOk = await updateUserName()
        if (nameOk) {
          nameUpdated = true
        } else {
          clearTimeout(safetyTimeout)
          toast.error(t('Failed to update username'), {
            duration: 4000,
            icon: '❌',
          })
          setIsSaving(false)
          return
        }
      }

      // Check and update email if changed
      if (emailChanged) {
        const emailOk = await updateEmail()
        if (emailOk) {
          emailUpdated = true
        } else {
          clearTimeout(safetyTimeout)
          toast.error(t('Failed to update email'), {
            duration: 4000,
            icon: '❌',
          })
          setIsSaving(false)
          return
        }
      }

      // Check and update preferences if changed
      if (prefChanged) {
        await updatePreferences()
      }

    // Show appropriate success message
    if (passwordUpdated) {
      toast.success(t('Password updated successfully. Please log in again.'), {
        duration: 4000,
        icon: '✅',
      })
      localStorage.clear()
      globalThis.location.href = '/login'
    } else {
      const successMessages = []
      if (nameUpdated) successMessages.push(t('Username updated'))
      if (emailUpdated) successMessages.push(t('Email updated'))
      if (prefChanged) successMessages.push(t('Preferences updated'))

      if (successMessages.length > 0) {
        toast.success(successMessages.join(', ') + ' ' + t('successfully!'), {
          duration: 3000,
          icon: '✅',
        })
      } else {
        toast.success(t('All settings saved successfully!'), {
          duration: 3000,
          icon: '✅',
        })
      }

      // Refresh user info in sidebar after successful save
      if (nameUpdated || emailUpdated) {
        await refreshUser()
      }
    }
    } catch (error) {
      console.error('Error saving settings:', error)
      toast.error(t('Failed to save settings'), {
        duration: 4000,
        icon: '❌',
      })
    } finally {
      clearTimeout(safetyTimeout)
      setIsSaving(false)
    }
  }

  // Delete account
  const handleDeleteAccount = async () => {
    if (!globalThis.confirm(t('Are you sure you want to permanently delete your account?'))) return
    try {
      const response = await fetch(`http://localhost:8080/api/settings/delete-account`, {
        method: 'DELETE',
        headers: getAuthHeaders(),
      })
      const result = await response.json()
      toast.success(result.message || t('Account deleted'), {
        duration: 3000,
        icon: '✅',
      })
      if (result.success) {
        localStorage.clear()
        globalThis.location.href = '/login'
      }
    } catch {
      toast.error(t('Failed to delete account'), {
        duration: 4000,
        icon: '❌',
      })
    }
  }

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white">
          {t('settingsTitle')}
        </h1>
        <p className="text-gray-600 dark:text-gray-300 mt-1">{t('manageAccount')}</p>
      </div>

      {/* Profile */}
      <Card className="bg-white/80 dark:bg-gray-800/80 border">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <User className="w-5 h-5" /> {t('profileInfo')}
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-medium">{t('User Name')}</label>
              <Input
                value={formData.name}
                onChange={(e) => handleInputChange('name', e.target.value)}
                autoComplete="off"
                placeholder={t('Enter your name')}
                name="settings-username"
                id="settings-username"
              />
            </div>
            <div>
              <label className="block text-sm font-medium">{t('Email Address')}</label>
              <Input
                type="email"
                value={formData.email}
                onChange={(e) => handleInputChange('email', e.target.value)}
                autoComplete="off"
                placeholder={t('Email address')}
                name="settings-email"
                id="settings-email"
                disabled
                className="bg-gray-50 dark:bg-gray-700 cursor-not-allowed"
              />
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Security */}
      <Card className="bg-white/80 dark:bg-gray-800/80 border">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Shield className="w-5 h-5" /> {t('security')}
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Current Password */}
          <div>
            <label className="block text-sm font-medium">{t('Current Password')}</label>
            <Input
              type="password"
              value={formData.currentPassword}
              onChange={(e) => handleInputChange('currentPassword', e.target.value)}
              placeholder={t('Enter current password')}
              autoComplete="off"
              name="settings-current-password"
              id="settings-current-password"
            />
          </div>

          {/* New Password */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-medium">{t('New Password')}</label>
              <Input
                type="password"
                value={formData.newPassword}
                onChange={(e) => handleInputChange('newPassword', e.target.value)}
                placeholder={t('Enter new password')}
                autoComplete="new-password"
                name="settings-new-password"
                id="settings-new-password"
              />
            </div>
            <div>
              <label className="block text-sm font-medium">{t('Confirm New Password')}</label>
              <Input
                type="password"
                value={formData.confirmPassword}
                onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                placeholder={t('Confirm new password')}
                autoComplete="new-password"
                name="settings-confirm-password"
                id="settings-confirm-password"
              />
            </div>
          </div>

          {/* Password Requirements */}
          <div className="bg-blue-50 dark:bg-blue-900/20 p-4 rounded-xl border border-blue-200/50 dark:border-blue-700/50">
            <div className="flex items-start gap-3">
              <Lock className="w-5 h-5 text-blue-600 dark:text-blue-400 mt-0.5" />
              <div>
                <h4 className="font-medium text-blue-900 dark:text-blue-100">{t('Password Requirements')}</h4>
                <ul className="text-sm text-blue-700 dark:text-blue-300 mt-1 space-y-1">
                  <li>• {t('At least 8 characters long')}</li>
                  <li>• {t('Contains uppercase and lowercase letters')}</li>
                  <li>• {t('Contains at least one number')}</li>
                  <li>• {t('Contains at least one special character (@#$%^&+=!)')}</li>
                </ul>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Appearance */}
      <Card className="bg-white/80 dark:bg-gray-800/80 border">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Palette className="w-5 h-5" /> {t('appearance')}
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Theme */}
            <div>
              <label className="block text-sm font-medium">{t('Theme')}</label>
              <div className="flex gap-3">
                <Button
                  variant={theme === 'light' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => theme === 'dark' && toggleTheme()}
                  className="flex-1"
                >
                  {t('Light')}
                </Button>
                <Button
                  variant={theme === 'dark' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => theme === 'light' && toggleTheme()}
                  className="flex-1"
                >
                  {t('Dark')}
                </Button>
              </div>
            </div>

            {/* Language */}
            <div>
              <label className="block text-sm font-medium">{t('Language')}</label>
              <Select value={preferences.language} onValueChange={handleLanguageChange}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="en">English</SelectItem>
                  <SelectItem value="zh">Chinese</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Account */}
      <Card className="bg-white/80 dark:bg-gray-800/80 border">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Database className="w-5 h-5" /> {t('accountManagement')}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-between p-4 rounded-xl bg-red-50/50 dark:bg-red-900/20">
            <div>
              <h4 className="font-medium text-red-900 dark:text-red-100">{t('deleteAccount')}</h4>
              <p className="text-sm text-red-600 dark:text-red-300">
                {t('Permanently delete your account and all data')}
              </p>
            </div>
            <Button
              onClick={handleDeleteAccount}
              variant="outline"
              size="sm"
              className="border-red-200 text-red-600 hover:bg-red-50"
            >
              <Trash2 className="w-4 h-4 mr-2" /> {t('Delete')}
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Save */}
      <div className="flex justify-end">
        <Button
          onClick={handleSaveAll}
          disabled={isSaving}
          className={`bg-gradient-to-r from-violet-500 to-purple-500 text-white rounded-2xl shadow-lg transition-all duration-200 ${
            isSaving
              ? 'opacity-70 cursor-not-allowed'
              : 'hover:shadow-xl hover:scale-105 active:scale-95'
          }`}
        >
          {isSaving ? (
            <>
              <div className="w-4 h-4 mr-2 border-2 border-white border-t-transparent rounded-full animate-spin" />
              {t('Saving...')}
            </>
          ) : (
            <>
              <Save className="w-4 h-4 mr-2" /> {t('saveAllSettings')}
            </>
          )}
        </Button>
      </div>
    </div>
  )
}
