import apiClient from './api'

import { type IProfileResume } from '../models'

const PROFILE_RESUME_API_URL = '/api/profiles/resumes'

export default class ProfileResumeService {
  retrieve(): Promise<IProfileResume> {
    return new Promise<IProfileResume>((resolve, reject) => {
      apiClient.get<IProfileResume>(PROFILE_RESUME_API_URL)
        .then(res => resolve(res.data))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }

  find(id: string): Promise<IProfileResume> {
    return new Promise<IProfileResume>((resolve, reject) => {
      apiClient
        .get(`${PROFILE_RESUME_API_URL}/${id}`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  upload(file: File): Promise<IProfileResume> {
    return new Promise<IProfileResume>((resolve, reject) => {
      const formData = new FormData()
      formData.append('profile_resume', file)
      apiClient.post(PROFILE_RESUME_API_URL, formData, { headers: { 'Content-Type': 'multipart/form-data' } })
        .then(res => resolve(res.data))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }

  download(): Promise<Blob> {
    return new Promise<Blob>((resolve, reject) => {
      apiClient
        .get(`${PROFILE_RESUME_API_URL}/download`, { responseType: 'blob' })
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  downloadById(id: string): Promise<Blob> {
    return new Promise<Blob>((resolve, reject) => {
      apiClient
        .get(`${PROFILE_RESUME_API_URL}/${id}/download`, { responseType: 'blob' })
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(PROFILE_RESUME_API_URL)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteById(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${PROFILE_RESUME_API_URL}/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }
}
