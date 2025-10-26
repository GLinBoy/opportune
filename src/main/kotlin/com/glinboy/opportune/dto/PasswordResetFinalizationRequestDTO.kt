package com.glinboy.opportune.dto

data class PasswordResetFinalizationRequestDTO(
	val code: String,
	val newPassword: String
)
