import type { WebhookTokenStatus } from './enumerations/webhook-token-status.model'

export interface IWebhookToken {
  id?: string
  endpointIdentifier?: string
  status?: keyof typeof WebhookTokenStatus | null
  createdAt?: Date
  revokedAt?: Date
  lastUsedAt?: Date
}

export interface IWebhookTokenGeneration {
  dto: IWebhookToken
  token: string
}
