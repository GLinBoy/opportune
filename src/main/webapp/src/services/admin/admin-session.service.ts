import apiClient from '../api'
import buildPaginationQueryOpts from '../../utils/pagination'
import type { IAdminSessionListItem } from '../../models'
import type { AxiosResponse } from 'axios'

const ADMIN_SESSIONS_API_URL = '/api/admin/sessions'

export default class AdminSessionService {
    list(paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<IAdminSessionListItem[]>> {
        return new Promise((resolve, reject) => {
            const query = buildPaginationQueryOpts(paginationQuery)
            const url = query ? `${ADMIN_SESSIONS_API_URL}?${query}` : ADMIN_SESSIONS_API_URL
            apiClient
                .get<IAdminSessionListItem[]>(url)
                .then(res => resolve(res))
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    revokeSession(id: string): Promise<void> {
        return new Promise((resolve, reject) => {
            apiClient
                .delete(`${ADMIN_SESSIONS_API_URL}/${id}`)
                .then(() => resolve())
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    bulkRevokeByUser(profileId: string): Promise<void> {
        return new Promise((resolve, reject) => {
            apiClient
                .post(`${ADMIN_SESSIONS_API_URL}/bulk-revoke/user/${profileId}`)
                .then(() => resolve())
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    bulkRevokeByIp(ip: string): Promise<void> {
        return new Promise((resolve, reject) => {
            apiClient
                .post(`${ADMIN_SESSIONS_API_URL}/bulk-revoke/ip`, null, { params: { ip } })
                .then(() => resolve())
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }
}
