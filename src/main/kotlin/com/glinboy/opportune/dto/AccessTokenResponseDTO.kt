package com.glinboy.opportune.dto

data class AccessTokenResponseDTO(
		val accessToken: String,
		val expiresIn: Long,
		val refreshToken: String,
		val refreshExpiresIn: Long,
		val tokenType: String = "Bearer",

)
