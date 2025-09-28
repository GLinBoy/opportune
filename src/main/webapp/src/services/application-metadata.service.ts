import apiClient from './api'

import buildPaginationQueryOpts from '../utils/pagination'
import { type IApplicationMetaData } from '../models'

const APPLICATION_METADATA_API_URL = '/api/applications/{application_id}/metadata'

export default class ApplicationMetaDataService {
  retrieve(applicationId: string, paginationQuery?: Record<string, unknown>): Promise<IApplicationMetaData[]> {
    return new Promise<IApplicationMetaData[]>((resolve, reject) => {
      const url = APPLICATION_METADATA_API_URL.replace('{application_id}', applicationId)
      const query = buildPaginationQueryOpts(paginationQuery)
      const fullUrl = query ? `${url}?${query}` : url
      apiClient.get<IApplicationMetaData[]>(fullUrl)
        .then(res => resolve(res.data))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  find(applicationId: string, id: string): Promise<IApplicationMetaData> {
    return new Promise<IApplicationMetaData>((resolve, reject) => {
      apiClient
        .get(`${APPLICATION_METADATA_API_URL.replace('{application_id}', applicationId)}/${id}`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  create(applicationId: string, entity: IApplicationMetaData): Promise<IApplicationMetaData> {
    return new Promise<IApplicationMetaData>((resolve, reject) => {
      apiClient
        .post(`${APPLICATION_METADATA_API_URL.replace('{application_id}', applicationId)}`, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  update(applicationId: string, entity: IApplicationMetaData): Promise<IApplicationMetaData> {
    return new Promise<IApplicationMetaData>((resolve, reject) => {
      apiClient
        .put(`${APPLICATION_METADATA_API_URL.replace('{application_id}', applicationId)}`, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(applicationId: string, id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${APPLICATION_METADATA_API_URL.replace('{application_id}', applicationId)}/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }
}
