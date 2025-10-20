import apiClient from './api'

import buildPaginationQueryOpts from '../utils/pagination'

import type { ISearchResult } from '../models'
import type { AxiosResponse } from 'axios'

const SEARCH_API_URL = '/api/search'

export default class SearchService {

  search(query: string, paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<ISearchResult[]>> {
    return new Promise<AxiosResponse<ISearchResult[]>>((resolve, reject) => {
      const q = buildPaginationQueryOpts({ query, ...paginationQuery })
      const url = q ? `${SEARCH_API_URL}?${q}` : `${SEARCH_API_URL}?query=${encodeURIComponent(query)}`
      apiClient.get<ISearchResult[]>(url)
        .then(res => resolve(res))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  searchCompanies(query: string, paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<ISearchResult[]>> {
    return new Promise<AxiosResponse<ISearchResult[]>>((resolve, reject) => {
      const q = buildPaginationQueryOpts({ query, ...paginationQuery })
      const url = q ? `${SEARCH_API_URL}/companies?${q}` : `${SEARCH_API_URL}/companies?query=${encodeURIComponent(query)}`
      apiClient.get<ISearchResult[]>(url)
        .then(res => resolve(res))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  searchApplications(query: string, paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<ISearchResult[]>> {
    return new Promise<AxiosResponse<ISearchResult[]>>((resolve, reject) => {
      const q = buildPaginationQueryOpts({ query, ...paginationQuery })
      const url = q ? `${SEARCH_API_URL}/applications?${q}` : `${SEARCH_API_URL}/applications?query=${encodeURIComponent(query)}`
      apiClient.get<ISearchResult[]>(url)
        .then(res => resolve(res))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

}
