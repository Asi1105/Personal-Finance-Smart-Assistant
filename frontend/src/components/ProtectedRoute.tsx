import { Navigate } from 'react-router'
import { useAuthStore } from '@/stores/authStore'
import { useAuthCheck } from '@/hooks/useAuthCheck'
import { Spinner } from '@/components/ui/loading'
import { authNotifications } from '@/utils/authNotifications'
import { useEffect, useRef } from 'react'

interface ProtectedRouteProps {
  readonly children: React.ReactNode
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading, token, isLoggingOut } = useAuthStore()
  const { isChecking } = useAuthCheck()
  const hasShownToast = useRef(false)
  const lastLogoutTime = useRef<number>(0)

  // Reset toast flag when user becomes authenticated
  useEffect(() => {
    if (isAuthenticated) {
      hasShownToast.current = false
    }
  }, [isAuthenticated])

  // Track logout time to prevent immediate toast after logout
  useEffect(() => {
    if (isLoggingOut) {
      lastLogoutTime.current = Date.now()
    }
  }, [isLoggingOut])

  // Show loading spinner while checking authentication or loading
  if (isChecking || isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-violet-50 via-cyan-50 to-emerald-50 dark:from-gray-900 dark:via-violet-900/20 dark:to-emerald-900/20">
        <div className="text-center">
          <Spinner size="lg" className="mx-auto mb-4" />
          <p className="text-gray-600 dark:text-gray-300">
            {isChecking ? 'Verifying authentication...' : 'Loading...'}
          </p>
        </div>
      </div>
    )
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    // Only show toast once per route access and not during logout
    // Also check if we're currently logging out to prevent toast during logout process
    // And check if we recently logged out (within 1 second) to prevent immediate toast
    const timeSinceLogout = Date.now() - lastLogoutTime.current
    const recentlyLoggedOut = timeSinceLogout < 1000 // 1 second
    
    if (!hasShownToast.current && !isLoggingOut && !recentlyLoggedOut) {
      hasShownToast.current = true
      
      // Show appropriate toast message based on whether user had a token
      if (token) {
        authNotifications.sessionExpired()
      } else {
        authNotifications.unauthorizedAccess()
      }
    }
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}