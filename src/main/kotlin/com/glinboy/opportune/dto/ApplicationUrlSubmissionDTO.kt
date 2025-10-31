package com.glinboy.opportune.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class ApplicationUrlSubmissionDTO(
	@field:NotBlank(message = "URL is required")
	@field:URL(message = "Invalid URL format")
	val url: String
)

