import apiClient from '../api'
import buildPaginationQueryOpts from '../../utils/pagination'
import type { IAdminUserListItem, IAdminUserDetail } from '../../models'
import type { AxiosResponse } from 'axios'

const ADMIN_USERS_API_URL = '/api/admin/users'

export default class AdminUserService {
    list(paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<IAdminUserListItem[]>> {
        return new Promise((resolve, reject) => {
            const query = buildPaginationQueryOpts(paginationQuery)
            const url = query ? `${ADMIN_USERS_API_URL}?${query}` : ADMIN_USERS_API_URL
            apiClient
                .get<IAdminUserListItem[]>(url)
                .then(res => resolve(res))
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    get(id: string): Promise<IAdminUserDetail> {
        return new Promise((resolve, reject) => {
            apiClient
                .get<IAdminUserDetail>(`${ADMIN_USERS_API_URL}/${id}`)
                .then(res => resolve(res.data))
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    updateStatus(id: string, status: string): Promise<void> {
        return new Promise((resolve, reject) => {
            apiClient
                .patch(`${ADMIN_USERS_API_URL}/${id}/status`, { status })
                .then(() => resolve())
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    updateRole(id: string, role: string): Promise<void> {
        return new Promise((resolve, reject) => {
            apiClient
                .patch(`${ADMIN_USERS_API_URL}/${id}/role`, { role })
                .then(() => resolve())
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    revokeSessions(id: string): Promise<void> {
        return new Promise((resolve, reject) => {
            apiClient
                .post(`${ADMIN_USERS_API_URL}/${id}/revoke-sessions`)
                .then(() => resolve())
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    deleteUser(id: string): Promise<void> {
        return new Promise((resolve, reject) => {
            apiClient
                .delete(`${ADMIN_USERS_API_URL}/${id}`)
                .then(() => resolve())
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }
}
