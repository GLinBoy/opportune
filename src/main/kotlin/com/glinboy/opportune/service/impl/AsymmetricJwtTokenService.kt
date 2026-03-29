package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.AccessTokenResponseDTO
import com.glinboy.opportune.dto.ProfileDTO
import com.glinboy.opportune.service.JwtTokenService
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class AsymmetricJwtTokenService: JwtTokenService {
	override fun generateTokens(
		profileDTO: ProfileDTO,
		rememberMe: Boolean
	): AccessTokenResponseDTO {
		TODO("Not yet implemented")
	}

	override fun refreshAccessToken(
		refreshJwt: Jwt,
		profileDTO: ProfileDTO
	): AccessTokenResponseDTO {
		TODO("Not yet implemented")
	}
}
