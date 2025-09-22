import apiClient from './api'

import buildPaginationQueryOpts from '../utils/pagination'

import { type IApplication } from '../models'
import type { AxiosResponse } from 'axios'

const COMPANY_APPLICATION_API_URL = '/api/companies/{company_id}/applications'

export default class CompanyApplicationService {

  retrieve(companyId: string, paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<IApplication[]>> {
    return new Promise<AxiosResponse<IApplication[]>>((resolve, reject) => {
      const query = buildPaginationQueryOpts(paginationQuery)
      const urlBase = COMPANY_APPLICATION_API_URL.replace('{company_id}', companyId)
      const url = query ? `${urlBase}?${query}` : urlBase
      apiClient.get<IApplication[]>(url)
        .then(res => resolve(res))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

}
