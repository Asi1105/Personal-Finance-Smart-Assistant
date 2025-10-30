import { useEffect, useState } from 'react'
import { useAuthStore } from '@/stores/authStore'
import { authApi } from '@/services/authService'
import { authNotifications } from '@/utils/authNotifications'

export function useAuthCheck() {
  const { token, logout, setLoading, isLoggingOut } = useAuthStore()
  const [isChecking, setIsChecking] = useState(true)

  useEffect(() => {
    const checkAuth = async () => {
      // If no token, user is not authenticated
      if (!token) {
        setIsChecking(false)
        return
      }

      // If token exists, always verify it with the backend
      setLoading(true)
      try {
        const userData = await authApi.getCurrentUser()
        
        // Update auth store with fresh user data
        useAuthStore.setState({
          user: userData,
          isAuthenticated: true,
          isLoading: false,
          error: null
        })
        
      } catch (error) {
        // Token is invalid or expired, logout and clear state
        console.warn('Token validation failed:', error)
        
        // Only show toast if we're not on login/register page and not during logout
        // This prevents showing toast when user is already on login page or during logout
        const currentPath = globalThis.location.pathname
        if (currentPath !== '/login' && currentPath !== '/register' && !isLoggingOut) {
          authNotifications.sessionExpired()
        }
        
        await logout()
      } finally {
        setLoading(false)
        setIsChecking(false)
      }
    }

    checkAuth()
  }, [token, logout, setLoading, isLoggingOut])

  return { isChecking }
}
