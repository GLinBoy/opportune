package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ProfileResumeDTO
import com.glinboy.opportune.entity.ProfileResume
import com.glinboy.opportune.mapper.ProfileResumeMapper
import com.glinboy.opportune.repository.ProfileResumeRepository
import com.glinboy.opportune.service.ProfileResumeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ProfileResumeServiceImpl(profileResumeRepository: ProfileResumeRepository, mapper: ProfileResumeMapper) :
	GenericServiceImpl<UUID, ProfileResume, ProfileResumeDTO, ProfileResumeRepository,
		ProfileResumeMapper>(profileResumeRepository, mapper), ProfileResumeService {

	override fun findByProfileId(profileId: UUID): Optional<ProfileResumeDTO> =
		repository.findByProfileId(profileId).map { mapper.toDto(it) }

	@Transactional
	override fun deleteByProfileId(profileId: UUID) =
		repository.deleteByProfileId(profileId)

}

