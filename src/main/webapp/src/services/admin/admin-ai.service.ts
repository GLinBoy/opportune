// src/main/webapp/src/services/admin/admin-ai.service.ts
import apiClient from '../api'
import buildPaginationQueryOpts from '../../utils/pagination'
import type { AdminAiQueueItem, AdminScoreDistribution } from '../../models'
import type { AxiosResponse } from 'axios'

const ADMIN_AI_API_URL = '/api/admin/ai'

export default class AdminAiService {
    getQueue(paginationQuery?: Record<string, unknown>): Promise<AxiosResponse<AdminAiQueueItem[]>> {
        return new Promise((resolve, reject) => {
            const query = buildPaginationQueryOpts(paginationQuery)
            const url = query ? `${ADMIN_AI_API_URL}/queue?${query}` : `${ADMIN_AI_API_URL}/queue`
            apiClient
                .get<AdminAiQueueItem[]>(url)
                .then(res => resolve(res))
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    retryJob(applicationId: string): Promise<void> {
        return new Promise((resolve, reject) => {
            apiClient
                .post(`${ADMIN_AI_API_URL}/retry/${applicationId}`)
                .then(() => resolve())
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }

    getScoreDistribution(): Promise<AxiosResponse<AdminScoreDistribution>> {
        return new Promise((resolve, reject) => {
            apiClient
                .get<AdminScoreDistribution>(`${ADMIN_AI_API_URL}/score-distribution`)
                .then(res => resolve(res))
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }
}
