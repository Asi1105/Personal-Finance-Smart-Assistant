import { useEffect } from 'react'
import { useAuthStore } from '@/stores/authStore'
import { authApi } from '@/services/authService'
import { usePageVisibility } from '@/hooks/usePageVisibility'
import { authNotifications } from '@/utils/authNotifications'

interface AuthProviderProps {
  readonly children: React.ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const { token, logout, setLoading, isLoggingOut } = useAuthStore()
  
  // Use page visibility hook for re-authentication when page becomes visible
  usePageVisibility()

  useEffect(() => {
    const initializeAuth = async () => {
      // Only check if we have a token
      if (!token) {
        return
      }

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
        console.warn('Initial auth check failed:', error)
        
        // Only show toast if we're not on login/register page and not during logout
        // This prevents showing toast when user is already on login page or during logout
        const currentPath = globalThis.location.pathname
        if (currentPath !== '/login' && currentPath !== '/register' && !isLoggingOut) {
          authNotifications.sessionExpired()
        }
        
        await logout()
      } finally {
        setLoading(false)
      }
    }

    initializeAuth()
  }, [token, logout, setLoading, isLoggingOut])

  return <>{children}</>
}
