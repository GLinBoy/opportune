package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ProfileResumeDTO
import java.util.*

interface ProfileResumeService : GenericService<UUID, ProfileResumeDTO> {
	fun findByProfileId(profileId: UUID): Optional<ProfileResumeDTO>
	fun deleteByProfileId(profileId: UUID): Unit
	fun findByProfileIdForCurrentUser(profileId: UUID): Optional<ProfileResumeDTO>
	fun deleteByProfileIdForCurrentUser(profileId: UUID): Unit
}

