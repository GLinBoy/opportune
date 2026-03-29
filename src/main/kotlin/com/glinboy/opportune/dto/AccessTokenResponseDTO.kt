package com.glinboy.opportune.dto

import com.glinboy.opportune.security.SecurityUtils

data class AccessTokenResponseDTO(
	val accessToken: String,
	val expiresIn: Long,
	val refreshToken: String,
	val refreshExpiresIn: Long,
	val tokenType: String = SecurityUtils.TYPE_TOKEN_BEARER,

	)
