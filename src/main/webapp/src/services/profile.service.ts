import apiClient from './api'

import buildPaginationQueryOpts from '../utils/pagination'

import { type IProfile, type IPasswordChangeRequest, type ISession } from '../models'
import type { AxiosResponse } from 'axios'

const PROFILE_API_URL = '/api/profiles'

export default class ProfileService {

  retrieve(paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<IProfile[]>> {
    return new Promise<AxiosResponse<IProfile[]>>((resolve, reject) => {
      const query = buildPaginationQueryOpts(paginationQuery)
      const url = query ? `${PROFILE_API_URL}?${query}` : PROFILE_API_URL
      apiClient.get<IProfile[]>(url)
        .then(res => resolve(res))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  find(id: string): Promise<IProfile> {
    return new Promise<IProfile>((resolve, reject) => {
      apiClient
        .get(`${PROFILE_API_URL}/${id}`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    });
  }

  create(entity: IProfile): Promise<IProfile> {
    return new Promise<IProfile>((resolve, reject) => {
      apiClient
        .post(PROFILE_API_URL, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  update(entity: IProfile): Promise<IProfile> {
    return new Promise<IProfile>((resolve, reject) => {
      apiClient
        .put(PROFILE_API_URL, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${PROFILE_API_URL}/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  getCurrentProfile(): Promise<IProfile> {
    return new Promise<IProfile>((resolve, reject) => {
      apiClient
        .get(`${PROFILE_API_URL}/me`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  changePassword(request: IPasswordChangeRequest): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .put(`${PROFILE_API_URL}/password/change`, request)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  confirmEmail(code: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .put(`${PROFILE_API_URL}/email/confirm?code=${encodeURIComponent(code)}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  verifyEmail(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .post(`${PROFILE_API_URL}/email/verify/request`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  getSessions(paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<ISession[]>> {
    return new Promise<AxiosResponse<ISession[]>>((resolve, reject) => {
      const query = buildPaginationQueryOpts(paginationQuery)
      const url = query ? `${PROFILE_API_URL}/sessions?${query}` : `${PROFILE_API_URL}/sessions`
      apiClient.get<ISession[]>(url)
        .then(res => resolve(res))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  terminateSession(refreshTokenId: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${PROFILE_API_URL}/sessions/${refreshTokenId}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

}
