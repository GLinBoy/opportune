package com.glinboy.opportune.service

import com.glinboy.opportune.dto.*
import org.springframework.security.core.userdetails.UserDetailsService
import java.time.Instant
import java.util.*

interface ProfileService : GenericService<UUID, ProfileDTO>, UserDetailsService {
	fun getCurrentProfile(): Optional<ProfileDTO>
	fun countAll(): Long
	fun getRegistrationTrend(from: Instant): List<UserRegistrationTrendPointDTO>
	fun getAccountStatusDistribution(): List<AccountStatusCountDTO>
	fun register(profileDTO: ProfileDTO)
	fun login(loginRequestDTO: LoginRequestDTO): AccessTokenResponseDTO
	fun refreshToken(refreshToken: String): AccessTokenResponseDTO
	fun confirmEmail(code: String)
	fun initiatePasswordReset(passwordResetInitiationRequestDTO: PasswordResetInitiationRequestDTO)
	fun finalizePasswordReset(passwordResetFinalizationRequestDTO: PasswordResetFinalizationRequestDTO)
	fun changePassword(passwordUpdateRequestDTO: PasswordUpdateRequestDTO)
	fun updateAvatarPath(id: UUID, path: String?)
	fun clearAvatarPath(id: UUID)
}
