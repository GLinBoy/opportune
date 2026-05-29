import apiClient from '../api'
import buildPaginationQueryOpts from '../../utils/pagination'
import type { IAdminCompanyListItem, IAdminCompanyUpdate } from '../../models'
import type { AxiosResponse } from 'axios'

const ADMIN_COMPANIES_API_URL = '/api/admin/companies'

export default class AdminCompanyService {
    list(paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<IAdminCompanyListItem[]>> {
        return new Promise((resolve, reject) => {
            const query = buildPaginationQueryOpts(paginationQuery)
            const url = query ? `${ADMIN_COMPANIES_API_URL}?${query}` : ADMIN_COMPANIES_API_URL
            apiClient
                .get<IAdminCompanyListItem[]>(url)
                .then(res => resolve(res))
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    updateStatus(id: string, status: string): Promise<IAdminCompanyListItem> {
        return new Promise((resolve, reject) => {
            apiClient
                .patch<IAdminCompanyListItem>(`${ADMIN_COMPANIES_API_URL}/${id}/status`, { status })
                .then(res => resolve(res.data))
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    updateDetails(id: string, dto: IAdminCompanyUpdate): Promise<IAdminCompanyListItem> {
        return new Promise((resolve, reject) => {
            apiClient
                .patch<IAdminCompanyListItem>(`${ADMIN_COMPANIES_API_URL}/${id}`, dto)
                .then(res => resolve(res.data))
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }
}
