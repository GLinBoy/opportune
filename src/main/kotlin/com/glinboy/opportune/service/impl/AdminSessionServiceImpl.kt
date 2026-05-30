package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.AdminSessionListItemDTO
import com.glinboy.opportune.entity.Session
import com.glinboy.opportune.enums.RevocationReason
import com.glinboy.opportune.repository.SessionRepository
import com.glinboy.opportune.service.AdminSessionService
import io.github.perplexhub.rsql.RSQLJPASupport
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
@Transactional(readOnly = true)
class AdminSessionServiceImpl(
	private val sessionRepository: SessionRepository,
) : AdminSessionService {

	private val log = LoggerFactory.getLogger(this::class.java)

	override fun listSessions(filter: String?, pageable: Pageable): Page<AdminSessionListItemDTO> {
		val page: Page<Session> = if (filter.isNullOrBlank()) {
			sessionRepository.findAll(pageable)
		} else {
			val spec: Specification<Session> = RSQLJPASupport.toSpecification(filter)
			sessionRepository.findAll(spec, pageable)
		}
		return page.map { toAdminDto(it) }
	}

	@Transactional
	override fun revokeSession(id: UUID) {
		if (!sessionRepository.existsById(id)) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")
		}
		sessionRepository.revokeSessionById(id, RevocationReason.ADMIN_ACTION)
		log.info("Admin revoked session {}", id)
	}

	@Transactional
	override fun bulkRevokeByUser(profileId: UUID) {
		sessionRepository.revokeAllSessions(profileId, RevocationReason.ADMIN_ACTION)
		log.info("Admin bulk-revoked all sessions for profile {}", profileId)
	}

	@Transactional
	override fun bulkRevokeByIp(ip: String) {
		sessionRepository.revokeAllSessionsByIp(ip, RevocationReason.ADMIN_ACTION)
		log.info("Admin bulk-revoked all sessions for IP {}", ip)
	}

	private fun toAdminDto(session: Session) = AdminSessionListItemDTO(
		refreshTokenId = session.refreshTokenId,
		status = session.status,
		lastActiveAt = session.lastActiveAt,
		clientIp = session.clientIp,
		clientGeo = session.clientGeo,
		os = session.os,
		browser = session.browser,
		isMobile = session.isMobile,
		loginAt = session.loginAt,
		revokedAt = session.revokedAt,
		revocationReason = session.revocationReason,
		profileId = session.profile?.id,
		userEmail = session.profile?.email,
		userForename = session.profile?.forename,
		userSurname = session.profile?.surname,
	)
}
