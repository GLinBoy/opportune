package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.dto.ApplicationDetailsDTO
import com.glinboy.opportune.dto.ApplicationUrlSubmissionDTO
import com.glinboy.opportune.entity.Application
import com.glinboy.opportune.enums.ApplicationStatus
import com.glinboy.opportune.mapper.ApplicationDetailsMapper
import com.glinboy.opportune.mapper.ApplicationMapper
import com.glinboy.opportune.projection.ApplicationProjection
import com.glinboy.opportune.repository.ApplicationRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.ApplicationService
import com.glinboy.opportune.service.JobDescriptionFetcherService
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ApplicationServiceImpl(
	applicationRepository: ApplicationRepository,
	mapper: ApplicationMapper,
	private val applicationDetailsMapper: ApplicationDetailsMapper,
	private val entityManager: EntityManager,
	private val jobDescriptionFetcherService: JobDescriptionFetcherService
) : GenericServiceImpl<UUID, Application, ApplicationDTO, ApplicationRepository,
	ApplicationMapper>(applicationRepository, mapper), ApplicationService {

	override fun getCompanyApplications(companyId: UUID, pageable: Pageable): Page<ApplicationDTO> =
		repository.findAll(
			Specification.allOf<Application>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("company").get<UUID>("id"), companyId)
				}, pageable
		)
			.map(mapper::toDto)

	override fun findAllApplications(pageable: Pageable): Page<ApplicationProjection> =
		repository.findAllApplications(pageable)

	override fun getApplicationDetails(id: UUID): Optional<ApplicationDetailsDTO> =
		repository.findApplicationDetailsById(id)
			.map { applicationDetailsMapper.toDto(it) }

	override fun currentUserSpecification(): Specification<Application> =
		createCurrentUserSpecification { it.get<UUID>("profile").get("id") }

	override fun validateOwnership(applicationDTO: ApplicationDTO) {
		if (applicationDTO.profileId != null &&
			applicationDTO.profileId != UUID.fromString(SecurityUtils.getCurrentUserLogin())) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to save this application")
		}
		applicationDTO.companyId?.let {
			val currentUserId = SecurityUtils.getCurrentUserLoginID()

			// Query parent entity directly using JPQL
			val query = entityManager.createQuery(
				"""
			SELECT COUNT(p) FROM Company p
			WHERE p.id = :parentId AND p.profile.id = :userId
			""".trimIndent(),
				Long::class.java
			)
			query.setParameter("parentId", it)
			query.setParameter("userId", currentUserId)

			val count = query.singleResult
			if (count == 0L) {
				throw ResponseStatusException(
					HttpStatus.FORBIDDEN,
					"Parent not found or you do not have permission to access this resource"
				)
			}
		}
	}

	override fun getCompanyApplicationsForCurrentUser(
		companyId: UUID,
		pageable: Pageable
	): Page<ApplicationDTO> =
		repository.findAll(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("company").get<UUID>("id"), companyId)
				}, pageable
		)
			.map(mapper::toDto)

	override fun findAllApplicationsForCurrentUser(pageable: Pageable): Page<ApplicationProjection> =
		repository
			.findAllApplicationsByProfileId(SecurityUtils.getCurrentUserLoginID(), pageable)

	override fun getApplicationDetailsForCurrentUser(id: UUID): Optional<ApplicationDetailsDTO> =
		repository
			.findApplicationDetailsByProfileIdAndId(SecurityUtils.getCurrentUserLoginID(), id)
			.map { applicationDetailsMapper.toDto(it) }

	@Transactional
	override fun submitApplicationUrl(submission: ApplicationUrlSubmissionDTO): Optional<ApplicationDTO> {
		return try {
			log.info("Submitting application URL: {}", submission.url)

			// Fetch job description content
			val content = jobDescriptionFetcherService.fetchJobDescription(submission.url)
			log.debug("Successfully fetched content from {} (source: {})", submission.url, content.sourceType)

			// Get current user's profile ID
			val currentUserId = SecurityUtils.getCurrentUserLoginID()

			// Check if application with this URL already exists for this user
			val existingApplication = repository.findOne(
				Specification.allOf<Application>()
					.and { root, _, criteriaBuilder ->
						criteriaBuilder.equal(root.get<UUID>("profile").get<UUID>("id"), currentUserId)
					}
					.and { root, _, criteriaBuilder ->
						criteriaBuilder.equal(root.get<String>("url"), submission.url)
					}
			)

			if (existingApplication.isPresent) {
				log.warn("Application with URL {} already exists for user {}", submission.url, currentUserId)
				throw ResponseStatusException(
					HttpStatus.CONFLICT,
					"An application with this URL already exists"
				)
			}

			// Create ApplicationDTO with fetched content
			val applicationDTO = ApplicationDTO(
				url = submission.url,
				title = content.title,
				rawContent = content.content,
				status = ApplicationStatus.INITIATED,
				profileId = currentUserId
			)

			// Save using the service's save method
			val savedApplication = save(applicationDTO)
			log.info("Created new application with ID {} from URL {}", savedApplication.id, submission.url)

			Optional.of(savedApplication)
		} catch (e: ResponseStatusException) {
			// Re-throw ResponseStatusException as-is
			throw e
		} catch (e: Exception) {
			log.error("Failed to submit application URL: {}", submission.url, e)
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"Failed to fetch job description from URL: ${e.message}"
			)
		}
	}

}
