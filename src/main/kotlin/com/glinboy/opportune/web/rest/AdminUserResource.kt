package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.AdminUserDetailDTO
import com.glinboy.opportune.dto.AdminUserListItemDTO
import com.glinboy.opportune.dto.AdminUserRoleUpdateDTO
import com.glinboy.opportune.dto.AdminUserStatusUpdateDTO
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.service.AdminUserService
import com.glinboy.opportune.service.FileService
import com.glinboy.opportune.util.PaginationUtil
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.core.io.Resource
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@RestController
@Secured("ROLE_ADMIN")
@RequestMapping("/api/admin/users")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class AdminUserResource(
	private val adminUserService: AdminUserService,
	private val profileRepository: ProfileRepository,
	private val fileService: FileService
) {

	@GetMapping
	@PageableAsQueryParam
	fun listUsers(
		@Parameter(hidden = true) pageable: Pageable,
		@RequestParam(value = "filter", required = false) filter: String?,
		request: HttpServletRequest,
	): ResponseEntity<List<AdminUserListItemDTO>> {
		val page = adminUserService.listUsers(filter, pageable)
		val headers: HttpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, request)
		headers.accessControlExposeHeaders = listOf(HttpHeaders.LINK, "X-Total-Count")
		return ResponseEntity(page.content, headers, HttpStatus.OK)
	}

	@GetMapping("/{id}")
	fun getUser(@PathVariable id: UUID): ResponseEntity<AdminUserDetailDTO> =
		ResponseEntity.ok(adminUserService.getUser(id))

	@PatchMapping("/{id}/status")
	fun updateStatus(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: AdminUserStatusUpdateDTO,
	): ResponseEntity<ProfileDTO> =
		ResponseEntity.ok(adminUserService.updateStatus(id, dto.status!!))

	@PatchMapping("/{id}/role")
	fun updateRole(
		@PathVariable id: UUID,
		@Valid @RequestBody dto: AdminUserRoleUpdateDTO,
	): ResponseEntity<ProfileDTO> =
		ResponseEntity.ok(adminUserService.updateRole(id, dto.role!!))

	@PostMapping("/{id}/revoke-sessions")
	fun revokeSessions(@PathVariable id: UUID): ResponseEntity<Void> {
		adminUserService.revokeSessions(id)
		return ResponseEntity.noContent().build()
	}

	@DeleteMapping("/{id}")
	fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
		adminUserService.deleteUser(id)
		return ResponseEntity.noContent().build()
	}

	@GetMapping("/{profileId}/avatar")
	fun getAvatar(@PathVariable profileId: UUID): ResponseEntity<Resource> {
		val profile = profileRepository.findById(profileId)
			.orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found") }
		val filename = profile.avatar
			?: return ResponseEntity.notFound().build()
		val resource = fileService.loadAvatar(profileId, filename)
		val contentType = try {
			Files.probeContentType(Path.of(filename)) ?: MediaType.APPLICATION_OCTET_STREAM_VALUE
		} catch (e: Exception) {
			MediaType.APPLICATION_OCTET_STREAM_VALUE
		}
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.body(resource)
	}
}
