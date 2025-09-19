package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ProfileResumeDTO
import com.glinboy.opportune.service.ProfileResumeService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/profiles")
class ProfileResumeResource(private val profileResumeService: ProfileResumeService) {

	@GetMapping("/{profile_id}/resumes", "/{profile_id}/resumes/{id}")
	fun getProfileResume(
		@PathVariable("profile_id") profileId: UUID,
		@PathVariable(name = "id") optionalId: Optional<UUID>
	): ResponseEntity<ProfileResumeDTO> =
		optionalId.map { profileResumeService.findById(it) }
			.orElseGet { profileResumeService.findByProfileId(profileId) }
			.map { ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(it) }
			.orElse(ResponseEntity.notFound().build())

	@DeleteMapping("/{profile_id}/resumes", "/{profile_id}/resumes/{id}")
	fun deleteProfileResume(
		@PathVariable("profile_id") profileId: UUID,
		@PathVariable(name = "id") optionalId: Optional<UUID>
	): ResponseEntity<Void> = optionalId.map { profileResumeService.delete(it) }
		.orElseGet { profileResumeService.deleteByProfileId(profileId) }
		.let { ResponseEntity.noContent().build<Void>() }

	@PostMapping("/{profile_id}/resumes", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun uploadProfileResume(
		@PathVariable("profile_id") profileId: UUID,
		@RequestParam("profile_resume") profileResume: MultipartFile,
		request: HttpServletRequest
	): ResponseEntity<ProfileResumeDTO> {
//		val location = URI.create("${request.requestURI}/${savedEntity.id}")
//		return ResponseEntity.created(location)
//			.contentType(MediaType.APPLICATION_JSON)
//			.body(savedEntity)
		// TODO implement file upload (FileService?)
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
	}
}

