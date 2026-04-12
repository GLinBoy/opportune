package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.SessionDTO
import com.glinboy.opportune.entity.Session
import com.glinboy.opportune.enums.RevocationReason
import com.glinboy.opportune.enums.SessionStatus
import com.glinboy.opportune.mapper.SessionMapper
import com.glinboy.opportune.repository.SessionRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.SessionService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.*

@Service
@Transactional(readOnly = true)
class SessionServiceImpl(
	private val repository: SessionRepository,
	private val mapper: SessionMapper
) : SessionService {

	private val log = LoggerFactory.getLogger(this::class.java)

	// TODO: Sort by: Last activity, active, and few deactivated sessions
	override fun getSessionsForCurrentUser(pageable: Pageable): Page<SessionDTO> =
		repository.findAll(
			Specification.allOf<Session>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("profile").get<UUID>("id"), SecurityUtils.getCurrentUserLoginID())
				}, pageable
		)
			.map(mapper::toDto)

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	override fun save(session: SessionDTO): SessionDTO =
		mapper.toDto(repository.save(mapper.createEntity(session)))

	override fun update(session: SessionDTO): SessionDTO =
		mapper.toDto(repository.save(mapper.createEntity(session)))

	override fun findByRefreshTokenId(refreshTokenId: UUID): SessionDTO? =
		repository.findOne { root, _, criteriaBuilder ->
			criteriaBuilder.equal(root.get<UUID>("refreshTokenId"), refreshTokenId)
		}.map(mapper::toDto).orElse(null)

	override fun findByAccessTokenId(accessTokenId: UUID): SessionDTO? =
		repository.findOne { root, _, criteriaBuilder ->
			criteriaBuilder.equal(root.get<UUID>("accessTokenId"), accessTokenId)
		}.map(mapper::toDto).orElse(null)

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	override fun terminateCurrentSession() {
		val jwt = SecurityContextHolder.getContext().authentication?.principal as? Jwt
			?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authenticated session found")
		val accessTokenId = jwt.id?.let(UUID::fromString)
			?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token carries no 'jti' claim")
		val session = findByAccessTokenId(accessTokenId)
			?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No active session found for the provided access token")
		revokeSession(session)
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	override fun terminateSession(refreshTokenId: UUID) {
		val session = findByRefreshTokenId(refreshTokenId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")
		revokeSession(session)
	}

	override fun terminateAllOtherSessions(reason: RevocationReason) {
		val jwt = SecurityContextHolder.getContext().authentication?.principal as? Jwt
			?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authenticated session found")
		val accessTokenId = jwt.id?.let(UUID::fromString)
			?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token carries no 'jti' claim")
		val session = findByAccessTokenId(accessTokenId)
			?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No active session found for the provided access token")
		repository.revokeAllSessionsExcept(session.profileId!!, reason, session.refreshTokenId!!)
	}

	override fun terminateAllSessionForProfile(profileId: UUID, reason: RevocationReason) {
		repository.revokeAllSessions(profileId, reason)
	}

	private fun revokeSession(session: SessionDTO) {
		if (session.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to terminate this session")
		}
		if (session.status != SessionStatus.ACTIVE) {
			throw ResponseStatusException(HttpStatus.CONFLICT, "Session is already terminated")
		}
		log.debug("Terminating session with refresh token ID: {}", session.refreshTokenId)
		repository.save(
			mapper.createEntity(
				session.copy(
					status = SessionStatus.REVOKED,
					revokedAt = Instant.now(),
					revocationReason = RevocationReason.USER_INITIATED
				)
			)
		)
	}
}
