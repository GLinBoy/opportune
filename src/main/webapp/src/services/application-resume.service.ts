import apiClient from './api'

import buildPaginationQueryOpts from '../utils/pagination'
import { type IApplicationResume } from '../models'

const APPLICATION_RESUME_API_URL = '/api/applications/{application_id}/resumes'

export default class ApplicationResumeService {
  retrieve(applicationId: string, paginationQuery?: Record<string, unknown>): Promise<IApplicationResume[]> {
    return new Promise<IApplicationResume[]>((resolve, reject) => {
      const url = APPLICATION_RESUME_API_URL.replace('{application_id}', applicationId)
      const query = buildPaginationQueryOpts(paginationQuery)
      const fullUrl = query ? `${url}?${query}` : url
      apiClient.get<IApplicationResume[]>(fullUrl)
        .then(res => resolve(res.data))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  find(applicationId: string, id: string): Promise<IApplicationResume> {
    return new Promise<IApplicationResume>((resolve, reject) => {
      apiClient
        .get(`${APPLICATION_RESUME_API_URL.replace('{application_id}', applicationId)}/${id}`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  upload(applicationId: string, file: File): Promise<IApplicationResume> {
    return new Promise<IApplicationResume>((resolve, reject) => {
      const formData = new FormData()
      formData.append('file', file)
      apiClient
        .post(`${APPLICATION_RESUME_API_URL.replace('{application_id}', applicationId)}`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  deleteAll(applicationId: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${APPLICATION_RESUME_API_URL.replace('{application_id}', applicationId)}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(applicationId: string, id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${APPLICATION_RESUME_API_URL.replace('{application_id}', applicationId)}/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }
}
