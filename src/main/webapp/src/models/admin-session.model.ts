import type { SessionStatus } from './enumerations/session-status.model'
import type { RevocationReason } from './enumerations/revocation-reason.model'

export interface IAdminSessionListItem {
    refreshTokenId?: string
    status?: keyof typeof SessionStatus | null
    lastActiveAt?: string
    clientIp?: string
    clientGeo?: string
    os?: string
    browser?: string
    isMobile?: boolean
    loginAt?: string
    revokedAt?: string
    revocationReason?: keyof typeof RevocationReason | null
    profileId?: string
    userEmail?: string
    userForename?: string
    userSurname?: string
}
