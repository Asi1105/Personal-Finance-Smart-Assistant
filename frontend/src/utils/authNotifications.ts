import toast from 'react-hot-toast'
import i18n from '@/i18n'


// Track active toasts to prevent duplicates
const activeToasts = new Set<string>()

// Auto-cleanup active toasts after a delay
const cleanupToasts = () => {
  setTimeout(() => {
    activeToasts.clear()
  }, 1000)
}

export const authNotifications = {
  /**
   * 🔒 Session expired
   */
  sessionExpired: () => {
    const toastId = 'session-expired'
    if (activeToasts.has(toastId)) return

    activeToasts.add(toastId)
    cleanupToasts()

    toast.error(i18n.t('sessionExpired'), {
      duration: 4000,
      icon: '🔒',
      id: toastId
    })
  },

  /**
   * Unauthorized access
   */
  unauthorizedAccess: () => {
    const toastId = 'unauthorized-access'
    if (activeToasts.has(toastId)) return

    activeToasts.add(toastId)
    cleanupToasts()

    toast.error(i18n.t('unauthorizedAccess'), {
      duration: 4000,
      icon: '🚫',
      id: toastId
    })
  },

  /**
   * Authentication failed
   */
  authenticationFailed: (message?: string) => {
    const toastId = 'auth-failed'
    if (activeToasts.has(toastId)) return

    activeToasts.add(toastId)
    cleanupToasts()

    toast.error(
      message || i18n.t('authenticationFailed'),
      {
        duration: 4000,
        icon: '❌',
        id: toastId
      }
    )
  },

  /**
   * Login success
   */
  loginSuccess: () => {
    const toastId = 'login-success'
    if (activeToasts.has(toastId)) return

    activeToasts.add(toastId)
    cleanupToasts()

    toast.success(i18n.t('loginSuccess'), {
      duration: 3000,
      icon: '✅',
      id: toastId
    })
  },

  /**
   * 👋 Logout success
   */
  logoutSuccess: () => {
    const toastId = 'logout-success'
    if (activeToasts.has(toastId)) return

    activeToasts.add(toastId)
    cleanupToasts()

    toast.success(i18n.t('logoutSuccess'), {
      duration: 3000,
      icon: '👋',
      id: toastId
    })
  },

  /**
   * 🧹 Clear all
   */
  clearAll: () => {
    activeToasts.clear()
    toast.dismiss()
  }
}
