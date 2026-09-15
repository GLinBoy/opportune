package com.glinboy.opportune.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL
import java.time.Instant

/**
 * Payload accepted by the webhook receive endpoint. [html] carries the page content captured by the
 * browser extension; when absent the backend falls back to fetching the page itself.
 */
data class WebhookSubmissionDTO(
	@field:NotBlank(message = "URL is required")
	@field:URL(message = "Invalid URL format")
	val url: String,

	val html: String? = null,

	val capturedAt: Instant? = null
)
