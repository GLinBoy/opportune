import apiClient from './api'

import { type IApplicationAttachment } from '../models'
import buildPaginationQueryOpts from '../utils/pagination'

const APPLICATION_ATTACHMENT_API_URL = '/api/applications/{application_id}/attachments'

export default class ApplicationAttachmentService {

  retrieve(applicationId: string, paginationQuery?: Record<string, unknown>): Promise<IApplicationAttachment[]> {
    return new Promise<IApplicationAttachment[]>((resolve, reject) => {
      const url = APPLICATION_ATTACHMENT_API_URL.replace('{application_id}', applicationId)
      const query = buildPaginationQueryOpts(paginationQuery)
      const fullUrl = query ? `${url}?${query}` : url
      apiClient.get<IApplicationAttachment[]>(fullUrl)
        .then(res => resolve(res.data))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }

  find(applicationId: string, id: string): Promise<IApplicationAttachment> {
    return new Promise<IApplicationAttachment>((resolve, reject) => {
      apiClient
        .get(`${APPLICATION_ATTACHMENT_API_URL.replace('{application_id}', applicationId)}/${id}`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  upload(applicationId: string, file: File): Promise<IApplicationAttachment> {
    return new Promise<IApplicationAttachment>((resolve, reject) => {
      const url = APPLICATION_ATTACHMENT_API_URL.replace('{application_id}', applicationId)
      const formData = new FormData()
      formData.append('file', file)
      apiClient.post(url, formData, { headers: { 'Content-Type': 'multipart/form-data' } })
        .then(res => resolve(res.data))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }

  deleteAll(applicationId: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(APPLICATION_ATTACHMENT_API_URL.replace('{application_id}', applicationId))
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(applicationId: string, id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${APPLICATION_ATTACHMENT_API_URL.replace('{application_id}', applicationId)}/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }
}
