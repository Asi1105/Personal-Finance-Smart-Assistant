// Unified error handling utility

export interface ApiError {
  message: string
  code?: string
  status?: number
  details?: Record<string, any>
}

export class AppError extends Error {
  public code?: string
  public status?: number
  public details?: Record<string, any>

  constructor(message: string, code?: string, status?: number, details?: Record<string, any>) {
    super(message)
    this.name = 'AppError'
    this.code = code
    this.status = status
    this.details = details
  }
}

// Error type constants
export const ErrorType = {
  NETWORK_ERROR: 'NETWORK_ERROR',
  VALIDATION_ERROR: 'VALIDATION_ERROR',
  AUTHENTICATION_ERROR: 'AUTHENTICATION_ERROR',
  AUTHORIZATION_ERROR: 'AUTHORIZATION_ERROR',
  NOT_FOUND_ERROR: 'NOT_FOUND_ERROR',
  SERVER_ERROR: 'SERVER_ERROR',
  UNKNOWN_ERROR: 'UNKNOWN_ERROR'
} as const

export type ErrorType = typeof ErrorType[keyof typeof ErrorType]

// Error handling function
export function handleApiError(error: any): AppError {
  // Network error
  if (!error.response) {
      return new AppError(
        'Network connection failed, please check network settings',
        ErrorType.NETWORK_ERROR,
        0
      )
  }

  const { status, data } = error.response

  // Handle errors based on HTTP status codes
  switch (status) {
    case 400:
      return new AppError(
        data?.error?.message || data?.message || 'Request parameter error',
        ErrorType.VALIDATION_ERROR,
        status,
        data?.error?.details || data?.details
      )
    
    case 401:
      return new AppError(
        data?.error?.message || data?.message || 'Unauthorized access, please login again',
        ErrorType.AUTHENTICATION_ERROR,
        status
      )
    
    case 403:
      return new AppError(
        data?.error?.message || data?.message || 'Insufficient permissions, cannot access this resource',
        ErrorType.AUTHORIZATION_ERROR,
        status
      )
    
    case 404:
      return new AppError(
        data?.error?.message || data?.message || 'Requested resource does not exist',
        ErrorType.NOT_FOUND_ERROR,
        status
      )
    
    case 422:
      return new AppError(
        data?.error?.message || data?.message || 'Data validation failed',
        ErrorType.VALIDATION_ERROR,
        status,
        data?.error?.details || data?.details
      )
    
    case 429:
      return new AppError(
        'Too many requests, please try again later',
        ErrorType.SERVER_ERROR,
        status
      )
    
    case 500:
      return new AppError(
        'Internal server error, please try again later',
        ErrorType.SERVER_ERROR,
        status
      )
    
    case 502:
    case 503:
    case 504:
      return new AppError(
        'Service temporarily unavailable, please try again later',
        ErrorType.SERVER_ERROR,
        status
      )
    
    default:
      return new AppError(
        data?.error?.message || data?.message || 'Unknown error, please try again later',
        ErrorType.UNKNOWN_ERROR,
        status
      )
  }
}

// Get user-friendly error message
export function getErrorMessage(error: any): string {
  if (error instanceof AppError) {
    return error.message
  }
  
  if (error instanceof Error) {
    return error.message
  }
  
  if (typeof error === 'string') {
    return error
  }
  
  return 'Unknown error occurred'
}

// Check if it is a specific type of error
export function isErrorType(error: any, type: ErrorType): boolean {
  if (error instanceof AppError) {
    return error.code === type
  }
  return false
}

// Format validation errors
export function formatValidationErrors(details: Record<string, any>): string[] {
  const errors: string[] = []
  
  for (const [field, messages] of Object.entries(details)) {
    if (Array.isArray(messages)) {
      errors.push(...messages.map(msg => `${field}: ${msg}`))
    } else if (typeof messages === 'string') {
      errors.push(`${field}: ${messages}`)
    }
  }
  
  return errors
}

// Error logging
export function logError(error: any, context?: string): void {
  const errorInfo = {
    message: getErrorMessage(error),
    code: error.code,
    status: error.status,
    details: error.details,
    context,
    timestamp: new Date().toISOString(),
    stack: error.stack
  }
  
  // Print detailed error information in development environment
  if (import.meta.env.DEV) {
    console.error('Error occurred:', errorInfo)
  }
  
  // In production environment, can send to error monitoring service
  // sendToErrorService(errorInfo)
}

