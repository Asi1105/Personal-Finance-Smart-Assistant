import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'
import { authNotifications } from '@/utils/authNotifications'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    throw error
  }
)

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Only logout if we're not already on the login page
      const currentPath = globalThis.location.pathname
      const { logout, isLoggingOut } = useAuthStore.getState()
      
      if (currentPath !== '/login' && currentPath !== '/register' && !isLoggingOut) {
        // Show toast notification for unauthorized access
        authNotifications.sessionExpired()
        
        // Clear auth state and redirect to login
        await logout()
        
        // Redirect to login page
        globalThis.location.href = '/login'
      }
    }
    throw error
  }
)

export default api