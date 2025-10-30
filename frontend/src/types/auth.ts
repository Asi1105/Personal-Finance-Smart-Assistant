// Authentication related DTO type definitions

// Login request DTO
export interface LoginRequest {
  email: string
  password: string
}

// Register request DTO
export interface RegisterRequest {
  name: string
  email: string
  password: string
}

// User information DTO
export interface UserDto {
  id: string
  name: string
  email: string
  createdAt: string
}

// Authentication response DTO
export interface AuthResponse {
  user: UserDto
  token: string
}

// Error response DTO
export interface ErrorResponse {
  message: string
  code: string
  details: Record<string, any>
}

// API response wrapper
export interface ApiResponse<T = any> {
  success: boolean
  data: T
  message: string
  error: ErrorResponse
}


