import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { authApi } from '@/services/authService'
import type { UserDto, LoginRequest, RegisterRequest } from '@/types/auth'
import toast from 'react-hot-toast'

interface AuthState {
  user: UserDto | null
  token: string | null
  isAuthenticated: boolean
  isLoading: boolean
  isLoggingOut: boolean
  error: string | null
  
  // Actions
  login: (credentials: LoginRequest) => Promise<void>
  register: (userData: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
  refreshUser: () => Promise<void>
  clearError: () => void
  setLoading: (loading: boolean) => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,
      isLoggingOut: false,
      error: null,

      login: async (credentials: LoginRequest) => {
        set({ isLoading: true, error: null })
        
        try {
          const response = await authApi.login(credentials)
          
          set({
            user: response.user,
            token: response.token,
            isAuthenticated: true,
            isLoading: false,
            error: null
          })
          
          toast.success('Login successful!')
        } catch (error: any) {
          set({
            user: null,
            token: null,
            isAuthenticated: false,
            isLoading: false,
            error: error.message
          })
          
          toast.error(error.message || 'Login failed')
          throw error
        }
      },

      register: async (userData: RegisterRequest) => {
        set({ isLoading: true, error: null })
        
        try {
          const response = await authApi.register(userData)
          
          set({
            user: response.user,
            token: response.token,
            isAuthenticated: true,
            isLoading: false,
            error: null
          })
          
          toast.success('Registration successful!')
        } catch (error: any) {
          set({
            user: null,
            token: null,
            isAuthenticated: false,
            isLoading: false,
            error: error.message
          })
          
          toast.error(error.message || 'Registration failed')
          throw error
        }
      },

      logout: async () => {
        set({ isLoading: true, isLoggingOut: true })
        
        try {
          await authApi.logout()
        } catch (error) {
          // Execute local logout even if API call fails
          console.warn('Logout API call failed:', error)
        } finally {
          // Show success toast first while isLoggingOut is still true
          toast.success('Logged out successfully')
          
          // Delay state clearing to ensure toast is displayed and other components don't interfere
          setTimeout(() => {
            set({
              user: null,
              token: null,
              isAuthenticated: false,
              isLoading: false,
              isLoggingOut: false,
              error: null
            })
          }, 100) // Small delay to ensure toast is processed
        }
      },

      refreshUser: async () => {
        try {
          const response = await authApi.getCurrentUser()
          set({ user: response })
          // Also update localStorage to keep it in sync
          if (response) {
            localStorage.setItem('userName', response.name || '')
            localStorage.setItem('email', response.email || '')
          }
        } catch (error) {
          console.error('Failed to refresh user info:', error)
        }
      },

      clearError: () => set({ error: null }),
      
      setLoading: (loading: boolean) => set({ isLoading: loading })
    }),
    {
      name: 'auth-storage',
      // Only persist necessary authentication information
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated
      })
    }
  )
)