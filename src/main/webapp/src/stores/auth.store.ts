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
  const refreshExpiresAt = ref<number | null>(
    localStorage.getItem('refreshExpiresAt') ? Number(localStorage.getItem('refreshExpiresAt')) : null
  )
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Computed
  const isAuthenticated = computed(() => {
    if (!accessToken.value || !expiresAt.value) return false
    return Date.now() < expiresAt.value
  })

  const roles = computed<string[]>(() => {
    if (!accessToken.value) return []
    try {
      const [, segment] = accessToken.value.split('.')
      if (!segment) return []
      const payload = JSON.parse(atob(segment.replace(/-/g, '+').replace(/_/g, '/')))
      return Array.isArray(payload.roles) ? payload.roles : []
    } catch {
      return []
    }
  })

  const isAdmin = computed(() => roles.value.includes('ROLE_ADMIN'))

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

      // Calculate expiration times (current time + expiresIn seconds)
      const expirationTime = Date.now() + response.expiresIn * 1000
      expiresAt.value = expirationTime
      const refreshExpirationTime = Date.now() + response.refreshExpiresIn * 1000
      refreshExpiresAt.value = refreshExpirationTime

      // Persist to localStorage
      localStorage.setItem('accessToken', response.accessToken)
      localStorage.setItem('refreshToken', response.refreshToken)
      localStorage.setItem('tokenType', response.tokenType)
      localStorage.setItem('expiresAt', expirationTime.toString())
      localStorage.setItem('refreshExpiresAt', refreshExpirationTime.toString())
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
    if (!refreshToken.value || (refreshExpiresAt.value && Date.now() >= refreshExpiresAt.value)) {
      clearAuth()
      throw new Error('No refresh token available')
    }

    try {
      const response: IAccessTokenResponse = await authService.refreshToken({ refreshToken: refreshToken.value })

      // Update access token (refresh token is not rotated, so keep its expiry unchanged)
      accessToken.value = response.accessToken
      tokenType.value = response.tokenType

      const expirationTime = Date.now() + response.expiresIn * 1000
      expiresAt.value = expirationTime

      // Persist to localStorage
      localStorage.setItem('accessToken', response.accessToken)
      localStorage.setItem('tokenType', response.tokenType)
      localStorage.setItem('expiresAt', expirationTime.toString())
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
    refreshExpiresAt.value = null
    error.value = null

    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('tokenType')
    localStorage.removeItem('expiresAt')
    localStorage.removeItem('refreshExpiresAt')
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
    roles,
    isAdmin,
    // Actions
    login,
    logout,
    refreshAccessToken,
    clearAuth,
  }
})
