package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.PasswordUpdateRequestDTO
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.dto.SessionDTO
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.FileService
import com.glinboy.opportune.service.ProfileService
import com.glinboy.opportune.service.SessionService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.core.io.Resource
import org.springframework.web.server.ResponseStatusException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@RestController
@RequestMapping("/api/profiles")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class ProfileResource(
	profileService: ProfileService,
	private val sessionService: SessionService,
	private val fileService: FileService,
	private val profileRepository: ProfileRepository
) : GenericResource<UUID, ProfileDTO, ProfileService>(profileService) {

	@GetMapping("/me")
	fun getCurrentProfile(): ResponseEntity<ProfileDTO>? =
		service.getCurrentProfile()
			.map { ResponseEntity.ok(it) }
			.orElse(ResponseEntity.notFound().build())

	@PageableAsQueryParam
	@GetMapping("/sessions")
	fun getSessions(@Parameter(hidden = true) pageable: Pageable,
									request: HttpServletRequest): ResponseEntity<List<SessionDTO>> {
		val page = sessionService.getSessionsForCurrentUser(pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@DeleteMapping("/sessions/{refreshTokenId}")
	fun terminateSession(@PathVariable refreshTokenId: UUID): ResponseEntity<Unit> {
		log.debug("REST request to terminate session with refresh token ID: {}", refreshTokenId)
		sessionService.terminateSession(refreshTokenId)
		return ResponseEntity.noContent().build()
	}

	@PutMapping("/password/change")
	fun changePassword(
		@Valid @RequestBody passwordUpdateRequestDTO: PasswordUpdateRequestDTO
	): ResponseEntity<Unit> {
		service.changePassword(passwordUpdateRequestDTO)
		return ResponseEntity.ok().build()
	}

	@PostMapping("/email/verify/request")
	fun verifyEmail(): ResponseEntity<Unit> {
		// TODO: Implement resend activation email
		return ResponseEntity.ok().build()
	}

	@PutMapping("/email/confirm")
	fun confirmEmail(@RequestParam code: String): ResponseEntity<Unit> {
			service.confirmEmail(code)
			return ResponseEntity.ok().build()
	}

	@PostMapping("/avatar", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun uploadAvatar(@RequestParam("avatar") file: MultipartFile): ResponseEntity<Map<String, String>> {
		val currentUserId = SecurityUtils.getCurrentUserLoginID()
		val filePath = fileService.uploadAvatar(currentUserId, file)
		profileRepository.updateAvatar(currentUserId, filePath)
		return ResponseEntity.ok(mapOf("avatarPath" to filePath))
	}

	@DeleteMapping("/avatar")
	fun deleteAvatar(): ResponseEntity<Unit> {
		val currentUserId = SecurityUtils.getCurrentUserLoginID()
		val profile = profileRepository.findById(currentUserId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found") }
		profile.avatar?.let { avatarPath ->
			fileService.deleteAvatar(avatarPath)
			profileRepository.updateAvatar(currentUserId, null)
		}
		return ResponseEntity.noContent().build()
	}

	@GetMapping("/avatar")
	fun getAvatar(): ResponseEntity<Resource> {
		val currentUserId = SecurityUtils.getCurrentUserLoginID()
		val profile = profileRepository.findById(currentUserId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found") }
		val avatarPath = profile.avatar
			?: return ResponseEntity.notFound().build()
		val resource = fileService.getAvatar(avatarPath)
		val contentType = try {
			Files.probeContentType(Path.of(avatarPath)) ?: MediaType.APPLICATION_OCTET_STREAM_VALUE
		} catch (e: Exception) {
			MediaType.APPLICATION_OCTET_STREAM_VALUE
		}
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.body(resource)
	}
}

