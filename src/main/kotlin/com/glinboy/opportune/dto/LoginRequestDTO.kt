package com.glinboy.opportune.dto

data class LoginRequestDTO(
	val email: String,
	val password: String,
	val rememberMe: Boolean = false
)
