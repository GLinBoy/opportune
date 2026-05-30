// src/main/webapp/src/services/admin/admin-settings.service.ts
import apiClient from '../api'
import type { AdminSettings } from '../../models'
import type { AxiosResponse } from 'axios'

const ADMIN_SETTINGS_API_URL = '/api/admin/settings'

export default class AdminSettingsService {
  get(): Promise<AxiosResponse<AdminSettings>> {
    return new Promise((resolve, reject) => {
      apiClient
        .get<AdminSettings>(ADMIN_SETTINGS_API_URL)
        .then(res => resolve(res))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }

  update(settings: AdminSettings): Promise<AxiosResponse<AdminSettings>> {
    return new Promise((resolve, reject) => {
      apiClient
        .patch<AdminSettings>(ADMIN_SETTINGS_API_URL, settings)
        .then(res => resolve(res))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }
}
