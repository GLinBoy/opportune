package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.AccessTokenResponseDTO
import com.glinboy.opportune.dto.LoginRequestDTO
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.service.ProfileService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class AuthResource(private val profileService: ProfileService) {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	fun register(@Valid @RequestBody profileDTO: ProfileDTO) {
		profileService.register(profileDTO)
	}

	@PostMapping("/auth/login")
	fun login(
		@Valid @RequestBody loginRequestDTO: LoginRequestDTO,
		response: HttpServletResponse
	): ResponseEntity<AccessTokenResponseDTO> {
		return ResponseEntity.ok(profileService.login(loginRequestDTO))
	}

	@PostMapping("/auth/token/refresh")
	@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
	fun refreshToken(): ResponseEntity<Void> {
		// TODO: Implement refresh token
		log.debug("REST request to refresh token")
		return ResponseEntity.ok().build()
	}

	@PostMapping("/auth/logout")
	@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
	fun logout(): ResponseEntity<Void> {
		// TODO: Implement logout
		log.debug("REST request to logout")
		return ResponseEntity.ok().build()
	}

	@PostMapping("/auth/password/reset/init")
	fun forgetPassword(): ResponseEntity<Void> {
		// TODO: Implement forget password - send email
		log.debug("REST request to forget password")
		return ResponseEntity.ok().build()
	}

	@PutMapping("/auth/password/reset/finish")
	fun resetPassword(): ResponseEntity<Void> {
		// TODO: Implement reset password
		log.debug("REST request to reset password")
		return ResponseEntity.ok().build()
	}
}
