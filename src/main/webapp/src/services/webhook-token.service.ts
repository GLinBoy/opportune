import apiClient from './api'
import { type IWebhookToken, type IWebhookTokenGeneration } from '../models'
import type { AxiosResponse } from 'axios'

const WEBHOOK_TOKEN_API_URL = '/api/webhook-tokens'

export default class WebhookTokenService {
  getWebhookTokens(): Promise<AxiosResponse<IWebhookToken[]>> {
    return new Promise<AxiosResponse<IWebhookToken[]>>((resolve, reject) => {
      apiClient
        .get<IWebhookToken[]>(WEBHOOK_TOKEN_API_URL)
        .then(res => resolve(res))
        .catch((error: unknown) => reject(error instanceof Error ? error : new Error(String(error))))
    })
  }

  generateWebhookToken(): Promise<IWebhookTokenGeneration> {
    return new Promise<IWebhookTokenGeneration>((resolve, reject) => {
      apiClient
        .post<IWebhookTokenGeneration>(WEBHOOK_TOKEN_API_URL)
        .then(res => resolve(res.data))
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }

  revokeWebhookToken(id: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      apiClient
        .delete(`${WEBHOOK_TOKEN_API_URL}/${id}`)
        .then(() => resolve())
        .catch((err: unknown) => reject(err instanceof Error ? err : new Error(String(err))))
    })
  }
}
