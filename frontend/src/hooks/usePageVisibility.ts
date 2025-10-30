import { useEffect } from 'react'
import { useAuthStore } from '@/stores/authStore'
import { authApi } from '@/services/authService'
import { authNotifications } from '@/utils/authNotifications'

export function usePageVisibility() {
  const { token, logout, setLoading, isLoggingOut } = useAuthStore()

  useEffect(() => {
    const handleVisibilityChange = async () => {
      // Only check if page becomes visible and we have a token
      if (document.visibilityState === 'visible' && token) {
        setLoading(true)
        try {
          // Verify token with backend
          const userData = await authApi.getCurrentUser()
          
          // Update auth store with fresh user data
          useAuthStore.setState({
            user: userData,
            isAuthenticated: true,
            isLoading: false,
            error: null
          })
          
        } catch (error) {
          // Token is invalid, clear auth state
          console.warn('Page visibility auth check failed:', error)
          
        // Only show toast if not during logout
        if (!isLoggingOut) {
          authNotifications.sessionExpired()
        }
          
          await logout()
        } finally {
          setLoading(false)
        }
      }
    }

    document.addEventListener('visibilitychange', handleVisibilityChange)
    
    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityChange)
    }
  }, [token, logout, setLoading, isLoggingOut])
}
