import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import AuthService from './auth.service'

// API configuration
const apiConfig: AxiosRequestConfig = {
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
}

// Create axios instance
const apiClient: AxiosInstance = axios.create(apiConfig)

const authService = new AuthService()

// Token refresh state
let isRefreshing = false
let failedQueue: Array<{
  resolve: (token: string) => void
  reject: (error: unknown) => void
}> = []

function processQueue(error: unknown, token: string | null = null): void {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error)
    } else {
      resolve(token!)
    }
  })
  failedQueue = []
}

function clearAuth(): void {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('tokenType')
  localStorage.removeItem('expiresAt')
  localStorage.removeItem('refreshExpiresAt')
}

// Request interceptor (for JWT when you implement it)
apiClient.interceptors.request.use(
  (config) => {
    // Add auth token when available
    const token = localStorage.getItem('accessToken')
    const tokenType = localStorage.getItem('tokenType')
    if (token && tokenType) {
      config.headers.Authorization = `${tokenType} ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor (for error handling and token refresh)
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response
  },
  async (error) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean }

    // Skip refresh for non-401 errors, already retried requests, or auth endpoints
    if (
      error.response?.status !== 401 ||
      originalRequest._retry ||
      originalRequest.url?.includes('/api/auth/token/refresh') ||
      originalRequest.url?.includes('/api/auth/login')
    ) {
      throw error
    }

    const refreshTokenValue = localStorage.getItem('refreshToken')
    if (!refreshTokenValue) {
      clearAuth()
      if (globalThis.location.pathname !== '/auth/login') {
        globalThis.location.href = '/auth/login'
      }
      throw error
    }

    // If already refreshing, queue this request
    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        failedQueue.push({
          resolve: (token: string) => {
            const tokenType = localStorage.getItem('tokenType')
            originalRequest._retry = true
            originalRequest.headers.Authorization = `${tokenType} ${token}`
            resolve(apiClient(originalRequest))
          },
          reject: (err: unknown) => {
            reject(err)
          },
        })
      })
    }

    isRefreshing = true
    originalRequest._retry = true

    try {
      const data = await authService.refreshToken({ refreshToken: refreshTokenValue })

      // Update stored tokens (only access token changes; refresh token is not rotated)
      const expirationTime = Date.now() + data.expiresIn * 1000
      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('tokenType', data.tokenType)
      localStorage.setItem('expiresAt', expirationTime.toString())

      // Retry queued requests
      processQueue(null, data.accessToken)

      // Retry original request
      originalRequest.headers.Authorization = `${data.tokenType} ${data.accessToken}`
      return apiClient(originalRequest)
    } catch (refreshError) {
      processQueue(refreshError, null)
      clearAuth()
      if (globalThis.location.pathname !== '/auth/login') {
        globalThis.location.href = '/auth/login'
      }
      throw refreshError
    } finally {
      isRefreshing = false
    }
  }
)

export default apiClient
