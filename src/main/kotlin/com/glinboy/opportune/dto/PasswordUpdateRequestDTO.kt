package com.glinboy.opportune.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PasswordUpdateRequestDTO(
	@field:NotBlank
	val currentPassword: String,

	@field:NotBlank
	@field:Size(min = 8, max = 100)
	val newPassword: String
)
