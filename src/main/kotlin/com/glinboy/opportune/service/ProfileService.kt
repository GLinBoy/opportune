package com.glinboy.opportune.service

import com.glinboy.opportune.dto.AccessTokenResponseDTO
import com.glinboy.opportune.dto.LoginRequestDTO
import com.glinboy.opportune.dto.PasswordResetFinalizationRequestDTO
import com.glinboy.opportune.dto.PasswordResetInitiationRequestDTO
import com.glinboy.opportune.dto.PasswordUpdateRequestDTO
import com.glinboy.opportune.dto.ProfileDTO
import org.springframework.security.core.userdetails.UserDetailsService
import java.util.*

interface ProfileService : GenericService<UUID, ProfileDTO>, UserDetailsService {
	fun getCurrentProfile(): Optional<ProfileDTO>
	fun register(profileDTO: ProfileDTO)
	fun login(loginRequestDTO: LoginRequestDTO): AccessTokenResponseDTO
	fun confirmEmail(code: String)
	fun initiatePasswordReset(passwordResetInitiationRequestDTO: PasswordResetInitiationRequestDTO)
	fun finalizePasswordReset(passwordResetFinalizationRequestDTO: PasswordResetFinalizationRequestDTO)
	fun changePassword(passwordUpdateRequestDTO: PasswordUpdateRequestDTO)
}

