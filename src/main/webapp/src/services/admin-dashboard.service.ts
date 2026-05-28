// src/main/webapp/src/services/admin-dashboard.service.ts
import apiClient from './api'
import type { AdminDashboardSummaryDto } from '../models'
import type { AxiosResponse } from 'axios'

const ADMIN_DASHBOARD_API_URL = '/api/admin/dashboard'

export default class AdminDashboardService {
    getSummary(): Promise<AxiosResponse<AdminDashboardSummaryDto>> {
        return new Promise<AxiosResponse<AdminDashboardSummaryDto>>((resolve, reject) => {
            apiClient
                .get<AdminDashboardSummaryDto>(`${ADMIN_DASHBOARD_API_URL}/summary`)
                .then(res => resolve(res))
                .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
        })
    }
}
