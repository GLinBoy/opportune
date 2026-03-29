package com.glinboy.opportune.dto

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequestDTO(
	@field:NotBlank
	val refreshToken: String
)

