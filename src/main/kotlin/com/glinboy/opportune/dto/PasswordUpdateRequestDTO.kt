package com.glinboy.opportune.dto

data class PasswordUpdateRequestDTO(
		val currentPassword: String,
		val newPassword: String
)
