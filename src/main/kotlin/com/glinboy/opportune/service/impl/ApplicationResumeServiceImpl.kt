package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationResumeDTO
import com.glinboy.opportune.entity.ApplicationResume
import com.glinboy.opportune.mapper.ApplicationResumeMapper
import com.glinboy.opportune.repository.ApplicationResumeRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.ApplicationResumeService
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ApplicationResumeServiceImpl(
	applicationResumeRepository: ApplicationResumeRepository,
	mapper: ApplicationResumeMapper,
	private val entityManager: EntityManager
) : GenericServiceImpl<UUID, ApplicationResume, ApplicationResumeDTO, ApplicationResumeRepository,
	ApplicationResumeMapper>(applicationResumeRepository, mapper), ApplicationResumeService {

	override fun findByApplicationId(applicationId: UUID): Optional<ApplicationResumeDTO> =
		repository.findOne(
			Specification.allOf<ApplicationResume>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("application").get<UUID>("id"), applicationId)
				}
		).map(mapper::toDto)

	@Transactional
	override fun deleteByApplicationId(applicationId: UUID) {
		val spec = Specification.allOf<ApplicationResume>()
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("application").get<UUID>("id"), applicationId)
			}
		repository.findOne(spec).ifPresent { repository.delete(it) }
	}

	override fun currentUserSpecification(): Specification<ApplicationResume> =
		createCurrentUserSpecification { it.get<ApplicationResume>("application").get<UUID>("profile").get("id") }

	override fun validateOwnership(applicationResumeDTO: ApplicationResumeDTO) {
		val parentId = applicationResumeDTO.applicationId ?: throw IllegalArgumentException("Parent ID is required")
		val currentUserId = SecurityUtils.getCurrentUserLoginID()

		// Query parent entity directly using JPQL
		val query = entityManager.createQuery(
			"""
			SELECT COUNT(p) FROM Application p
			WHERE p.id = :parentId AND p.profile.id = :userId
			""".trimIndent(),
			Long::class.java
		)
		query.setParameter("parentId", parentId)
		query.setParameter("userId", currentUserId)

		val count = query.singleResult
		if (count == 0L) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"Parent not found or you do not have permission to access this resource"
			)
		}
	}

	override fun findByApplicationIdForCurrentUser(applicationId: UUID): Optional<ApplicationResumeDTO> =
		repository.findOne(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("application").get<UUID>("id"), applicationId)
				}
		).map(mapper::toDto)

	override fun deleteByApplicationIdForCurrentUser(applicationId: UUID) {
		val spec = currentUserSpecification()
			.and { root, _, criteriaBuilder ->
				criteriaBuilder.equal(root.get<UUID>("application").get<UUID>("id"), applicationId)
			}
		repository.findOne(spec).ifPresent { repository.delete(it) }
	}
}
