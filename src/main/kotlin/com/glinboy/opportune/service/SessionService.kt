package com.glinboy.opportune.service

import com.glinboy.opportune.dto.SessionDTO
import com.glinboy.opportune.enums.RevocationReason
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface SessionService {
	fun getSessionsForCurrentUser(pageable: Pageable): Page<SessionDTO>
	fun save(session: SessionDTO): SessionDTO
	fun update(session: SessionDTO): SessionDTO
	fun findByRefreshTokenId(refreshTokenId: UUID): SessionDTO?
	fun findByAccessTokenId(accessTokenId: UUID): SessionDTO?
	fun countActiveSessions(): Long
	fun terminateCurrentSession()
	fun terminateSession(refreshTokenId: UUID)
	fun terminateAllOtherSessions(reason: RevocationReason)
	fun terminateAllSessionForProfile(profileId: UUID, reason: RevocationReason)
}
