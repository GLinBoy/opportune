import apiClient from './api'

import { type IProfileResume } from '../models'

const PROFILE_RESUME_API_URL = '/api/profiles/{profile_id}/resumes'

export default class ProfileResumeService {
  retrieve(profileId: string): Promise<IProfileResume[]> {
    return new Promise<IProfileResume[]>((resolve, reject) => {
      const url = PROFILE_RESUME_API_URL.replace('{profile_id}', profileId)
      apiClient.get<IProfileResume[]>(url)
        .then(res => resolve(res.data))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }

  find(profileId: string, id: string): Promise<IProfileResume> {
    return new Promise<IProfileResume>((resolve, reject) => {
      apiClient
        .get(`${PROFILE_RESUME_API_URL.replace('{profile_id}', profileId)}/${id}`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  upload(profileId: string, file: File): Promise<IProfileResume> {
    return new Promise<IProfileResume>((resolve, reject) => {
      const url = PROFILE_RESUME_API_URL.replace('{profile_id}', profileId)
      const formData = new FormData()
      formData.append('file', file)
      apiClient.post(url, formData, { headers: { 'Content-Type': 'multipart/form-data' } })
        .then(res => resolve(res.data))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }

  deleteAll(profileId: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(PROFILE_RESUME_API_URL.replace('{profile_id}', profileId))
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(profileId: string, id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${PROFILE_RESUME_API_URL.replace('{profile_id}', profileId)}/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }
}
