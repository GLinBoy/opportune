package com.glinboy.opportune.service

import com.glinboy.opportune.dto.AccessTokenResponseDTO
import com.glinboy.opportune.dto.LoginRequestDTO
import com.glinboy.opportune.dto.ProfileDTO
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.*

interface ProfileService : GenericService<UUID, ProfileDTO>, UserDetailsService {
	fun getCurrentProfile(): Optional<ProfileDTO>
	fun register(profileDTO: ProfileDTO)
	fun login(loginRequestDTO: LoginRequestDTO): AccessTokenResponseDTO
	fun confirmEmail(code: String)
}

