package com.glinboy.opportune.web.rest

import com.glinboy.opportune.config.OpenApiConfiguration
import com.glinboy.opportune.dto.WebhookTokenDTO
import com.glinboy.opportune.dto.WebhookTokenGenerationDTO
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.WebhookTokenService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * Manages the webhook tokens owned by the authenticated user.
 *
 * The raw token value is returned exactly once by [generate]; afterwards only metadata is exposed.
 * Authentication is handled by the default chain (the `/api` prefix requires `ROLE_USER`); this
 * resource is deliberately never opened with `permitAll`.
 */
@RestController
@RequestMapping("/api/webhook-tokens")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION_NAME)
class WebhookTokenResource(
	private val webhookTokenService: WebhookTokenService
) {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	@PostMapping
	fun generate(): ResponseEntity<WebhookTokenGenerationDTO> {
		val profileId = SecurityUtils.getCurrentUserLoginID()
		val generated = webhookTokenService.generate(profileId)
		log.debug("REST request to generate webhook token with ID: {}", generated.dto.id)
		return ResponseEntity.status(HttpStatus.CREATED).body(generated)
	}

	@GetMapping
	fun list(): ResponseEntity<List<WebhookTokenDTO>> {
		val profileId = SecurityUtils.getCurrentUserLoginID()
		return ResponseEntity.ok(webhookTokenService.listByProfile(profileId))
	}

	@DeleteMapping("/{id}")
	fun revoke(@PathVariable id: UUID): ResponseEntity<Unit> {
		val profileId = SecurityUtils.getCurrentUserLoginID()
		log.debug("REST request to revoke webhook token with ID: {}", id)
		webhookTokenService.revoke(id, profileId)
		return ResponseEntity.noContent().build()
	}
}
