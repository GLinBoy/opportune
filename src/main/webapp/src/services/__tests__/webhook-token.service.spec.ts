import { describe, it, expect, vi, beforeEach } from 'vitest'

const apiClientMock = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  delete: vi.fn()
}))

vi.mock('../api', () => ({ default: apiClientMock }))

import WebhookTokenService from '../webhook-token.service'

describe('WebhookTokenService', () => {
  const service = new WebhookTokenService()

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('lists webhook tokens without exposing token values', async () => {
    const response = {
      data: [{ id: 'token-1', endpointIdentifier: 'endpoint-1', status: 'ACTIVE' }]
    }
    apiClientMock.get.mockResolvedValue(response)

    await expect(service.getWebhookTokens()).resolves.toBe(response)
    expect(apiClientMock.get).toHaveBeenCalledWith('/api/webhook-tokens')
  })

  it('generates a webhook token and returns the raw token once', async () => {
    const payload = {
      dto: { id: 'token-1', endpointIdentifier: 'endpoint-1', status: 'ACTIVE' },
      token: 'opw_raw_token'
    }
    apiClientMock.post.mockResolvedValue({ data: payload })

    await expect(service.generateWebhookToken()).resolves.toEqual(payload)
    expect(apiClientMock.post).toHaveBeenCalledWith('/api/webhook-tokens')
  })

  it('revokes a webhook token by id', async () => {
    apiClientMock.delete.mockResolvedValue({})

    await expect(service.revokeWebhookToken('token-1')).resolves.toBeUndefined()
    expect(apiClientMock.delete).toHaveBeenCalledWith('/api/webhook-tokens/token-1')
  })
})
