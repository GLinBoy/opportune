package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.PasswordUpdateRequestDTO
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.service.ProfileService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/profiles")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class ProfileResource(profileService: ProfileService) :
	GenericResource<UUID, ProfileDTO, ProfileService>(profileService) {

	@GetMapping("/me")
	fun getCurrentProfile(): ResponseEntity<ProfileDTO> =
		service.getCurrentProfile()
			.map { ResponseEntity.ok(it) }
			.orElse(ResponseEntity.notFound().build())

	@PutMapping("/password/change")
	fun changePassword(
		@Valid @RequestBody passwordUpdateRequestDTO: PasswordUpdateRequestDTO
	): ResponseEntity<Void> {
		service.changePassword(passwordUpdateRequestDTO)
		return ResponseEntity.ok().build()
	}

	@PostMapping("/email/verify/request")
	fun verifyEmail(): ResponseEntity<Void> {
		// TODO: Implement resend activation email
		return ResponseEntity.ok().build()
	}

	@PutMapping("/email/confirm")
	fun confirmEmail(@RequestParam code: String): ResponseEntity<Void> {
			service.confirmEmail(code)
			return ResponseEntity.ok().build()
	}
}

