import apiClient from './api'

import buildPaginationQueryOpts from '../utils/pagination'
import { type ICompanyMetadata } from '../models'

const COMPANY_METADATA_API_URL = '/api/companies/{company_id}/metadata'

export default class CompanyMetadataService {
  retrieve(companyId: string, paginationQuery?: Record<string, unknown>): Promise<ICompanyMetadata[]> {
    return new Promise<ICompanyMetadata[]>((resolve, reject) => {
      const url = COMPANY_METADATA_API_URL.replace('{company_id}', companyId)
      const query = buildPaginationQueryOpts(paginationQuery)
      const fullUrl = query ? `${url}?${query}` : url
      apiClient.get<ICompanyMetadata[]>(fullUrl)
        .then(res => resolve(res.data))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  find(companyId: string, id: string): Promise<ICompanyMetadata> {
    return new Promise<ICompanyMetadata>((resolve, reject) => {
      apiClient
        .get(`${COMPANY_METADATA_API_URL.replace('{company_id}', companyId)}/${id}`)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  create(companyId: string, entity: ICompanyMetadata): Promise<ICompanyMetadata> {
    return new Promise<ICompanyMetadata>((resolve, reject) => {
      apiClient
        .post(`${COMPANY_METADATA_API_URL.replace('{company_id}', companyId)}`, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  update(companyId: string, entity: ICompanyMetadata): Promise<ICompanyMetadata> {
    return new Promise<ICompanyMetadata>((resolve, reject) => {
      apiClient
        .put(`${COMPANY_METADATA_API_URL.replace('{company_id}', companyId)}`, entity)
        .then(res => { resolve(res.data) })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }

  delete(companyId: string, id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${COMPANY_METADATA_API_URL.replace('{company_id}', companyId)}/${id}`)
        .then(() => { resolve() })
        .catch((err: unknown) => { reject(err instanceof Error ? err : new Error(String(err))) })
    })
  }
}
