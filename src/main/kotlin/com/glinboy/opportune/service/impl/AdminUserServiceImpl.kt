package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.AdminUserDetailDTO
import com.glinboy.opportune.dto.AdminUserListItemDTO
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.RevocationReason
import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.mapper.ProfileMapper
import com.glinboy.opportune.mapper.SessionMapper
import com.glinboy.opportune.repository.ApplicationRepository
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.repository.SessionRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.AdminUserService
import cz.jirutka.rsql.parser.RSQLParserException
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
class AdminUserServiceImpl(
	private val profileRepository: ProfileRepository,
	private val profileMapper: ProfileMapper,
	private val applicationRepository: ApplicationRepository,
	private val sessionRepository: SessionRepository,
	private val sessionMapper: SessionMapper,
) : AdminUserService {

	private val log = LoggerFactory.getLogger(this::class.java)

	override fun listUsers(filter: String?, pageable: Pageable): Page<AdminUserListItemDTO> {
		val page: Page<Profile> = if (filter.isNullOrBlank()) {
			profileRepository.findAll(pageable)
		} else {
			val spec: Specification<Profile> = try {
				RSQLJPASupport.toSpecification(filter)
			} catch (e: RSQLParserException) {
				throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid search filter syntax")
			}
			profileRepository.findAll(spec, pageable)
		}
		return page.map { profile ->
			AdminUserListItemDTO(
				id = profile.id,
				email = profile.email,
				forename = profile.forename,
				surname = profile.surname,
				jobTitle = profile.jobTitle,
				status = profile.status,
				roles = profile.roles,
				emailVerification = profile.emailVerification,
				lastLogin = profile.lastLogin,
				createdDate = profile.createdDate,
				lastModifiedDate = profile.lastModifiedDate,
			)
		}
	}

	override fun getUser(id: UUID): AdminUserDetailDTO {
		val profile = profileRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }
		val appCount = applicationRepository.countByProfile_Id(id)
		val sessions = sessionRepository.findAllByProfile_Id(id)
			.map(sessionMapper::toDto)
		return AdminUserDetailDTO(
			profile = profileMapper.toDto(profile),
			applicationCount = appCount,
			sessions = sessions,
		)
	}

	@Transactional
	override fun updateStatus(id: UUID, status: AccountStatus): ProfileDTO {
		if (!profileRepository.existsById(id)) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		}
		profileRepository.updateStatusById(id, status)
		if (status == AccountStatus.SUSPENDED) {
			sessionRepository.revokeAllSessions(id, RevocationReason.ACCOUNT_SUSPENDED)
		}
		return profileMapper.toDto(
			profileRepository.findById(id).orElseThrow()
		)
	}

	@Transactional
	override fun updateRole(id: UUID, role: Role): ProfileDTO {
		if (role != Role.ROLE_ADMIN && role != Role.ROLE_USER) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ROLE_ADMIN or ROLE_USER are valid targets")
		}
		val currentUserId = SecurityUtils.getCurrentUserLoginID()
		if (id == currentUserId) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change your own role")
		}
		val profile = profileRepository.findById(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }
		val newRoles: Set<Role> = when (role) {
			Role.ROLE_ADMIN -> setOf(Role.ROLE_USER, Role.ROLE_ADMIN)
			else -> setOf(Role.ROLE_USER)
		}
		val updated = profileRepository.save(profile.copy(roles = newRoles))
		return profileMapper.toDto(updated)
	}

	@Transactional
	override fun revokeSessions(id: UUID) {
		if (!profileRepository.existsById(id)) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		}
		sessionRepository.revokeAllSessions(id, RevocationReason.ADMIN_ACTION)
		log.info("Admin revoked all sessions for user $id")
	}

	@Transactional
	override fun deleteUser(id: UUID) {
		val currentUserId = SecurityUtils.getCurrentUserLoginID()
		if (id == currentUserId) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete your own account")
		}
		if (!profileRepository.existsById(id)) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
		}
		sessionRepository.revokeAllSessions(id, RevocationReason.ADMIN_ACTION)
		profileRepository.deleteById(id)
		log.info("Admin deleted user $id")
	}
}
