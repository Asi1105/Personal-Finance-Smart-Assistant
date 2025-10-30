import { api } from '@/lib/axios'
import { handleApiError, logError } from '@/utils/errorHandler'
import type {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  ApiResponse
} from '@/types/auth'

// Authentication API service class
export class AuthService {
  // User login
  static async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await api.post<ApiResponse<AuthResponse>>('/auth/login', credentials)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Login failed')
      }

      const authData = response.data.data

      // Save token (first login or re-login)
      if (authData?.token) {
        localStorage.setItem('token', authData.token)
        localStorage.setItem('email', authData.user?.email || '')
        localStorage.setItem('userId', String(authData.user?.id || ''))
        localStorage.setItem('userName', authData.user?.name || '') //
      }

      return authData
    } catch (error: any) {
      if (error.response?.status === 401) {
        const errorMessage =
          error.response?.data?.error?.message ||
          error.response?.data?.message ||
          'Authentication failed: Invalid email or password'
        throw new Error(errorMessage)
      } else if (error.response?.status === 404) {
        const errorMessage =
          error.response?.data?.error?.message ||
          error.response?.data?.message ||
          'Authentication failed: User not found'
        throw new Error(errorMessage)
      } else if (error.response?.status === 400) {
        const errorMessage =
          error.response?.data?.error?.message ||
          error.response?.data?.message ||
          'Authentication failed: Invalid request format'
        throw new Error(errorMessage)
      } else if (!error.response) {
        throw new Error('Authentication failed: Network connection error')
      }

      throw AuthService.handleError(error, 'Login')
    }
  }

  // User registration
  static async register(userData: RegisterRequest): Promise<AuthResponse> {
    try {
      const response = await api.post<ApiResponse<AuthResponse>>('/auth/register', userData)

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Registration failed')
      }

      const authData = response.data.data

      // Save token immediately after successful registration to ensure state synchronization
      if (authData.token) {
        localStorage.setItem('token', authData.token)
        localStorage.setItem('email', authData.user?.email || '')
        localStorage.setItem('userId', String(authData.user?.id || ''))
        localStorage.setItem('userName', authData.user?.name || '')

      }

      return authData
    } catch (error: any) {
      throw AuthService.handleError(error, 'Registration')
    }
  }

  // Logout
  static async logout(): Promise<void> {
    try {
      await api.post('/auth/logout')
    } catch (error: any) {
      console.warn('Logout request failed:', error.message)
    } finally {
      // Clear local credentials
      localStorage.removeItem('token')
      localStorage.removeItem('email')
      localStorage.removeItem('userId')
      localStorage.removeItem('userName')
    }
  }

  // Get current user information
  static async getCurrentUser(): Promise<AuthResponse['user']> {
    try {
      const response = await api.get<ApiResponse<AuthResponse['user']>>('/auth/me')

      if (!response.data.success || !response.data.data) {
        throw new Error(response.data.message || 'Failed to get user info')
      }

      return response.data.data
    } catch (error: any) {
      throw AuthService.handleError(error, 'Get current user')
    }
  }

  // Unified error handling
  private static handleError(error: any, context: string): Error {
    logError(error, context)
    return handleApiError(error)
  }
}

// Export unified API
export const authApi = {
  login: AuthService.login,
  register: AuthService.register,
  logout: AuthService.logout,
  getCurrentUser: AuthService.getCurrentUser
}
