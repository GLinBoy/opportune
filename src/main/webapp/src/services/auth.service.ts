import axios from 'axios'

import type { AxiosResponse } from 'axios'
import type { IProfile, ILoginRequest, IAccessTokenResponse, IRefreshTokenRequest, IPasswordResetInitiationRequest, IPasswordResetFinalizationRequest } from '../models'

const AUTH_API_URL = '/api/auth'

// Standalone axios instance without interceptors to avoid circular dependency with apiClient
const authClient = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

export default class AuthService {

  login(payload: ILoginRequest): Promise<IAccessTokenResponse> {
    return new Promise<IAccessTokenResponse>((resolve, reject) => {
      authClient
        .post(`${AUTH_API_URL}/login`, payload)
        .then((res: AxiosResponse<IAccessTokenResponse>) => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  logout(): Promise<void> {
    const token = localStorage.getItem('accessToken')
    const tokenType = localStorage.getItem('tokenType')
    const headers: Record<string, string> = {}
    if (token && tokenType) {
      headers.Authorization = `${tokenType} ${token}`
    }
    return new Promise<void>((resolve, reject) => {
      authClient
        .post(`${AUTH_API_URL}/logout`, null, { headers })
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  refreshToken(payload: IRefreshTokenRequest): Promise<IAccessTokenResponse> {
    return new Promise<IAccessTokenResponse>((resolve, reject) => {
      authClient
        .post(`${AUTH_API_URL}/token/refresh`, payload)
        .then((res: AxiosResponse<IAccessTokenResponse>) => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  forgetPassword(payload: IPasswordResetInitiationRequest): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      authClient
        .post(`${AUTH_API_URL}/password/reset/init`, payload)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  resetPassword(payload: IPasswordResetFinalizationRequest): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      authClient
        .put(`${AUTH_API_URL}/password/reset/finish`, payload)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  register(profile: IProfile): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      authClient
        .post('/api/register', profile)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

}
