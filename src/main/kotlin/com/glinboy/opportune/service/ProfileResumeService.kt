package com.glinboy.opportune.service

import com.glinboy.opportune.dto.ProfileResumeDTO
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface ProfileResumeService : GenericService<UUID, ProfileResumeDTO> {
	fun saveFile(profileResumeFile: MultipartFile): ProfileResumeDTO
	fun findByProfileId(profileId: UUID): Optional<ProfileResumeDTO>
	fun deleteByProfileId(profileId: UUID): Unit
	fun findForCurrentUser(): Optional<ProfileResumeDTO>
	fun deleteForCurrentUser(): Unit
	fun getFileResourceForCurrentUser(id: UUID): Pair<Resource, ProfileResumeDTO>
	fun getFileResourceForCurrentUser(): Pair<Resource, ProfileResumeDTO>
}

