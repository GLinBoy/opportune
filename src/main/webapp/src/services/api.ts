import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'

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
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('tokenType')
      localStorage.removeItem('expiresAt')
      // Redirect to login
      if (window.location.pathname !== '/auth/login') {
        window.location.href = '/auth/login'
      }
    }
    return Promise.reject(error)
  }
)

export default apiClient
