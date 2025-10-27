package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ApplicationResumeDTO
import com.glinboy.opportune.entity.ApplicationResume
import com.glinboy.opportune.mapper.ApplicationResumeMapper
import com.glinboy.opportune.repository.ApplicationResumeRepository
import com.glinboy.opportune.service.ApplicationResumeService
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ApplicationResumeServiceImpl(
	applicationResumeRepository: ApplicationResumeRepository,
	mapper: ApplicationResumeMapper
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
		repository.delete(
			Specification.allOf<ApplicationResume>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("application").get<UUID>("id"), applicationId)
				}
		)
	}

	override fun currentUserSpecification(): Specification<ApplicationResume> =
		createCurrentUserSpecification { it.get<ApplicationResume>("application").get<UUID>("profile").get("id") }

	override fun findByApplicationIdForCurrentUser(applicationId: UUID): Optional<ApplicationResumeDTO> =
		repository.findOne(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("application").get<UUID>("id"), applicationId)
				}
		).map(mapper::toDto)

	override fun deleteByApplicationIdForCurrentUser(applicationId: UUID) {
		repository.delete(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("application").get<UUID>("id"), applicationId)
				}
		)
	}
}
