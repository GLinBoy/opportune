package com.glinboy.opportune.service

import com.glinboy.opportune.dto.AccessTokenResponseDTO
import com.glinboy.opportune.dto.ProfileDTO
import org.springframework.security.oauth2.jwt.Jwt

interface JwtTokenService {
	fun generateTokens(profileDTO: ProfileDTO, rememberMe: Boolean): AccessTokenResponseDTO
	fun refreshAccessToken(refreshJwt: Jwt, profileDTO: ProfileDTO): AccessTokenResponseDTO
}
