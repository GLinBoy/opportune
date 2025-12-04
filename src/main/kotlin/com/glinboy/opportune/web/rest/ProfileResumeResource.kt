package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.ProfileResumeDTO
import com.glinboy.opportune.service.ProfileResumeService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/api/profiles/resumes")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class ProfileResumeResource(private val profileResumeService: ProfileResumeService) {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	@GetMapping("", "/{id}")
	fun getProfileResume(
		@PathVariable(name = "id") optionalId: Optional<UUID>
	): ResponseEntity<ProfileResumeDTO> =
		optionalId.map { profileResumeService.findByIdForCurrentUser(it) }
			.orElseGet { profileResumeService.findForCurrentUser() }
			.map { ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(it) }
			.orElse(ResponseEntity.notFound().build())

	@DeleteMapping("", "/{id}")
	fun deleteProfileResume(
		@PathVariable(name = "id") optionalId: Optional<UUID>
	): ResponseEntity<Unit> = optionalId
		.map { profileResumeService.deleteForCurrentUser(it) }
		.orElseGet { profileResumeService.deleteForCurrentUser() }
		.let { ResponseEntity.noContent().build() }

	@PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun uploadProfileResume(
		@RequestParam("profile_resume") profileResumeFile: MultipartFile,
		request: HttpServletRequest
	): ResponseEntity<ProfileResumeDTO> {
		val savedProfileResumeDTO: ProfileResumeDTO = profileResumeService.saveFile(profileResumeFile)
		val location = URI.create("${request.requestURI}/${savedProfileResumeDTO.id}")
		return ResponseEntity.created(location)
			.contentType(MediaType.APPLICATION_JSON)
			.body(savedProfileResumeDTO)
	}

	@GetMapping("/download", "/{id}/download")
	fun downloadProfileResume(
		@PathVariable(name = "id") optionalId: Optional<UUID>
	): ResponseEntity<Resource> {
		val (resource, profileResumeDTO) = optionalId
			.map { profileResumeService.getFileResourceForCurrentUser(it) }
			.orElseGet { profileResumeService.getFileResourceForCurrentUser() }
		val contentType = profileResumeDTO.contentType ?: MediaType.APPLICATION_OCTET_STREAM_VALUE
		val filename = profileResumeDTO.name ?: "resume"
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
			.body(resource)
	}
}

