package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.ProfileResumeDTO
import com.glinboy.opportune.entity.ProfileResume
import com.glinboy.opportune.mapper.ProfileResumeMapper
import com.glinboy.opportune.repository.ProfileResumeRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.FileService
import com.glinboy.opportune.service.ProfileResumeService
import org.springframework.core.io.Resource
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ProfileResumeServiceImpl(
	profileResumeRepository: ProfileResumeRepository,
	mapper: ProfileResumeMapper,
	val fileService: FileService
) : GenericServiceImpl<UUID, ProfileResume, ProfileResumeDTO, ProfileResumeRepository,
		ProfileResumeMapper>(profileResumeRepository, mapper), ProfileResumeService {

	@Transactional
	override fun saveFile(profileResumeFile: MultipartFile): ProfileResumeDTO {
		// Delete existing resume if present
		deleteForCurrentUser()
		repository.flush()

		val path = fileService.uploadResume(profileResumeFile)
		val profileResumeDTO = ProfileResumeDTO(
			name = profileResumeFile.originalFilename,
			path = path,
			contentType = profileResumeFile.contentType,
			contentLength = profileResumeFile.size,
			profileId = SecurityUtils.getCurrentUserLoginID()
		)
		return save(profileResumeDTO)
	}

	override fun findByProfileId(profileId: UUID): Optional<ProfileResumeDTO> =
		repository.findOne(
			Specification.allOf<ProfileResume>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("profile").get<UUID>("id"), profileId)
				}
		).map(mapper::toDto)

	@Transactional
	override fun deleteByProfileId(profileId: UUID) {
		val profileResume = repository.findOne(
			Specification.allOf<ProfileResume>()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("profile").get<UUID>("id"), profileId)
				}
		)
		profileResume.ifPresent { resume ->
			resume.path?.let { fileService.deleteFile(it) }
			repository.delete(resume)
		}
	}

	override fun currentUserSpecification(): Specification<ProfileResume> =
		createCurrentUserSpecification { it.get<ProfileResume>("profile").get("id") }

	override fun validateOwnership(profileResumeDTO: ProfileResumeDTO) {
		if (profileResumeDTO.profileId != null && profileResumeDTO.profileId != SecurityUtils.getCurrentUserLoginID()) {
			throw ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"You are not allowed to save or update Profile Resume for another user"
			)
		}
	}

	override fun findForCurrentUser(): Optional<ProfileResumeDTO> =
		repository.findOne(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("profile").get<UUID>("id"), SecurityUtils.getCurrentUserLoginID())
				}
		).map(mapper::toDto)

	@Transactional
	override fun deleteForCurrentUser() {
		val profileResume = repository.findOne(
			currentUserSpecification()
				.and { root, _, criteriaBuilder ->
					criteriaBuilder.equal(root.get<UUID>("profile").get<UUID>("id"), SecurityUtils.getCurrentUserLoginID())
				}
		)
		profileResume.ifPresent { resume ->
			resume.path?.let { fileService.deleteFile(it) }
			repository.delete(resume)
		}
	}

	override fun getFileResourceForCurrentUser(id: UUID): Pair<Resource, ProfileResumeDTO> {
		val profileResumeDTO = findByIdForCurrentUser(id)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Profile resume not found") }
		val path = profileResumeDTO.path
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "File path not found")
		val resource = fileService.loadFileAsResource(path)
		return Pair(resource, profileResumeDTO)
	}

	override fun getFileResourceForCurrentUser(): Pair<Resource, ProfileResumeDTO> {
		val profileResumeDTO = findForCurrentUser()
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Profile resume not found") }
		val path = profileResumeDTO.path
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "File path not found")
		val resource = fileService.loadFileAsResource(path)
		return Pair(resource, profileResumeDTO)
	}

}

