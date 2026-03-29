import type { SessionStatus } from './enumerations/session-status.model'
import type { RevocationReason } from './enumerations/revocation-reason.model'

export interface ISession {
  refreshTokenId?: string
  accessTokenId?: string
  accessTokenExpiration?: Date
  refreshTokenExpiration?: Date
  status?: keyof typeof SessionStatus | null
  lastActiveAt?: Date
  clientAgent?: string
  clientIp?: string
  clientGeo?: string
  deviceId?: string
  deviceType?: string
  os?: string
  browser?: string
  loginAt?: Date
  revokedAt?: Date
  revocationReason?: keyof typeof RevocationReason | null
  lastRefreshedAt?: Date
  profileId?: string
  mobile?: boolean
}
