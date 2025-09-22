import apiClient from './api'

import buildPaginationQueryOpts from '../utils/pagination'

import { type IApplication } from '../models'
import type { AxiosResponse } from 'axios'

const APPLICATION_API_URL = '/api/applications'

export default class ApplicationService {

  retrieve(paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<IApplication[]>> {
    return new Promise<AxiosResponse<IApplication[]>>((resolve, reject) => {
      const query = buildPaginationQueryOpts(paginationQuery)
      const url = query ? `${APPLICATION_API_URL}?${query}` : APPLICATION_API_URL
      apiClient.get<IApplication[]>(url)
        .then(res => resolve(res))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  find(id: string): Promise<IApplication> {
    return new Promise<IApplication>((resolve, reject) => {
      apiClient
        .get(`${APPLICATION_API_URL}/${id}`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    });
  }

  create(entity: IApplication): Promise<IApplication> {
    return new Promise<IApplication>((resolve, reject) => {
      apiClient
        .post(APPLICATION_API_URL, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  update(entity: IApplication): Promise<IApplication> {
    return new Promise<IApplication>((resolve, reject) => {
      apiClient
        .put(APPLICATION_API_URL, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${APPLICATION_API_URL}/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

}
