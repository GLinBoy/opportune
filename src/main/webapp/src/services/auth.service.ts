import apiClient from './api'

import type { AxiosResponse } from 'axios'
import type { IProfile, ILoginRequest, IAccessTokenResponse } from '../models'

const AUTH_API_URL = '/api/auth'

export default class AuthService {

  login(payload: ILoginRequest): Promise<IAccessTokenResponse> {
    return new Promise<IAccessTokenResponse>((resolve, reject) => {
      apiClient
        .post(`${AUTH_API_URL}/login`, payload)
        .then((res: AxiosResponse<IAccessTokenResponse>) => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  logout(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .post(`${AUTH_API_URL}/logout`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  refreshToken(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .post(`${AUTH_API_URL}/token/refresh`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  forgetPassword(email: { email: string }): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .post(`${AUTH_API_URL}/password/reset/init`, email)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  resetPassword(payload: { key: string; newPassword: string }): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .put(`${AUTH_API_URL}/password/reset/finish`, payload)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  register(profile: IProfile): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .post('/api/register', profile)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

}
