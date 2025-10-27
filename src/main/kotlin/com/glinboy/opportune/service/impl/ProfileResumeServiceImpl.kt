package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ProfileResumeDTO
import com.glinboy.opportune.entity.ProfileResume
import com.glinboy.opportune.mapper.ProfileResumeMapper
import com.glinboy.opportune.repository.ProfileResumeRepository
import com.glinboy.opportune.service.ProfileResumeService
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ProfileResumeServiceImpl(profileResumeRepository: ProfileResumeRepository, mapper: ProfileResumeMapper) :
	GenericServiceImpl<UUID, ProfileResume, ProfileResumeDTO, ProfileResumeRepository,
		ProfileResumeMapper>(profileResumeRepository, mapper), ProfileResumeService {

	override fun findByProfileId(profileId: UUID): Optional<ProfileResumeDTO> =
		repository.findOne(
			Specification.allOf<ProfileResume>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("profile").get<UUID>("id"), profileId)
				}
		).map(mapper::toDto)

	@Transactional
	override fun deleteByProfileId(profileId: UUID) {
		repository.delete(
			Specification.allOf<ProfileResume>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("profile").get<UUID>("id"), profileId)
				}
		)
	}

	override fun currentUserSpecification(): Specification<ProfileResume> =
		createCurrentUserSpecification { it.get<ProfileResume>("profile").get("id") }

	override fun findByProfileIdForCurrentUser(profileId: UUID): Optional<ProfileResumeDTO> =
		repository.findOne(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("profile").get<UUID>("id"), profileId)
				}
		).map(mapper::toDto)

	override fun deleteByProfileIdForCurrentUser(profileId: UUID) {
		repository.delete(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("profile").get<UUID>("id"), profileId)
				}
		)
	}

}

