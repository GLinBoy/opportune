import apiClient from './api'

import buildPaginationQueryOpts from '../utils/pagination'

import { type IApplicationTimeline } from '../models'
import type { AxiosResponse } from 'axios'

const APPLICATION_TIMELINE_API_URL = '/api/applications/{application_id}/timelines'

export default class ApplicationTimelineService {

  retrieve(applicationId: string, paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<IApplicationTimeline[]>> {
    return new Promise<AxiosResponse<IApplicationTimeline[]>>((resolve, reject) => {
      const query = buildPaginationQueryOpts(paginationQuery)
      const urlBase = APPLICATION_TIMELINE_API_URL.replace('{application_id}', applicationId)
      const url = query ? `${urlBase}?${query}` : urlBase
      apiClient.get<IApplicationTimeline[]>(url)
        .then(res => resolve(res))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  find(applicationId: string, id: string): Promise<IApplicationTimeline> {
    return new Promise<IApplicationTimeline>((resolve, reject) => {
      apiClient
        .get(`${APPLICATION_TIMELINE_API_URL.replace('{application_id}', applicationId)}/${id}`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  create(applicationId: string, entity: IApplicationTimeline): Promise<IApplicationTimeline> {
    return new Promise<IApplicationTimeline>((resolve, reject) => {
      apiClient
        .post(`${APPLICATION_TIMELINE_API_URL.replace('{application_id}', applicationId)}`, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  update(applicationId: string, entity: IApplicationTimeline): Promise<IApplicationTimeline> {
    return new Promise<IApplicationTimeline>((resolve, reject) => {
      apiClient
        .put(`${APPLICATION_TIMELINE_API_URL.replace('{application_id}', applicationId)}`, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(applicationId: string, id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${APPLICATION_TIMELINE_API_URL.replace('{application_id}', applicationId)}/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

}
