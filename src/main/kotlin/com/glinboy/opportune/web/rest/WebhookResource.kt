package com.glinboy.opportune.web.rest

import com.glinboy.opportune.dto.ApplicationDTO
import com.glinboy.opportune.dto.WebhookSubmissionDTO
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.ApplicationService
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * Receives captured pages from the browser extension. Authentication is handled entirely by the
 * dedicated webhook security chain ([com.glinboy.opportune.config.WebhookSecurityConfiguration]):
 * the token and the `{endpointId}` path segment must resolve to the same ACTIVE webhook token row,
 * which also determines the owning profile used for scoping.
 */
@RestController
@RequestMapping("/api/webhook")
open class WebhookResource(private val applicationService: ApplicationService) {

	@PostMapping("/{endpointId}")
	fun receive(
		@PathVariable endpointId: String,
		@Valid @RequestBody submission: WebhookSubmissionDTO
	): ResponseEntity<ApplicationDTO> {
		val profileId = SecurityUtils.getCurrentUserLoginID()
		val created = applicationService.createApplicationFromPage(profileId, submission.url, submission.html)
		return ResponseEntity.created(URI.create("/api/applications/${created.id}"))
			.contentType(MediaType.APPLICATION_JSON)
			.body(created)
	}
}
