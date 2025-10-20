import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import AuthService from '../services/auth.service'
import type { ILoginRequest, IAccessTokenResponse } from '../models'

const authService = new AuthService()

export const useAuthStore = defineStore('auth', () => {
  // State
  const accessToken = ref<string | null>(localStorage.getItem('accessToken'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const tokenType = ref<string | null>(localStorage.getItem('tokenType'))
  const expiresAt = ref<number | null>(
    localStorage.getItem('expiresAt') ? Number(localStorage.getItem('expiresAt')) : null
  )
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Computed
  const isAuthenticated = computed(() => {
    if (!accessToken.value || !expiresAt.value) return false
    return Date.now() < expiresAt.value
  })

  // Actions
  async function login(credentials: ILoginRequest): Promise<void> {
    isLoading.value = true
    error.value = null

    try {
      const response: IAccessTokenResponse = await authService.login(credentials)

      // Store tokens
      accessToken.value = response.accessToken
      refreshToken.value = response.refreshToken
      tokenType.value = response.tokenType

      // Calculate expiration time (current time + expiresIn seconds)
      const expirationTime = Date.now() + response.expiresIn * 1000
      expiresAt.value = expirationTime

      // Persist to localStorage
      localStorage.setItem('accessToken', response.accessToken)
      localStorage.setItem('refreshToken', response.refreshToken)
      localStorage.setItem('tokenType', response.tokenType)
      localStorage.setItem('expiresAt', expirationTime.toString())
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Login failed'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function logout(): Promise<void> {
    try {
      await authService.logout()
    } catch (err) {
      console.error('Logout error:', err)
    } finally {
      // Clear state and storage regardless of API call result
      clearAuth()
    }
  }

  async function refreshAccessToken(): Promise<void> {
    try {
      await authService.refreshToken()
      // Token refresh will update the cookie/header automatically
    } catch (err) {
      console.error('Token refresh failed:', err)
      clearAuth()
      throw err
    }
  }

  function clearAuth(): void {
    accessToken.value = null
    refreshToken.value = null
    tokenType.value = null
    expiresAt.value = null
    error.value = null

    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('tokenType')
    localStorage.removeItem('expiresAt')
  }



  // Initialize: Check if token is expired on store creation
  if (accessToken.value && expiresAt.value && Date.now() >= expiresAt.value) {
    clearAuth()
  }

  return {
    // State
    accessToken,
    refreshToken,
    tokenType,
    expiresAt,
    isLoading,
    error,
    // Computed
    isAuthenticated,
    // Actions
    login,
    logout,
    refreshAccessToken,
    clearAuth,
  }
})
