// src/main/webapp/src/services/dashboard.service.ts
import apiClient from './api'
import type { UserDashboardSummaryDto } from '../models'
import type { AxiosResponse } from 'axios'

const DASHBOARD_API_URL = '/api/dashboard'

export default class DashboardService {

  getUserSummary(): Promise<AxiosResponse<UserDashboardSummaryDto>> {
    return new Promise<AxiosResponse<UserDashboardSummaryDto>>((resolve, reject) => {
      apiClient
        .get<UserDashboardSummaryDto>(`${DASHBOARD_API_URL}/summary`)
        .then(res => resolve(res))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }
}
