package com.glinboy.opportune.service.impl

import com.glinboy.opportune.config.ApplicationProperties
import com.glinboy.opportune.dto.*
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.RevocationReason
import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.mapper.ProfileMapper
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.service.JwtTokenService
import com.glinboy.opportune.service.MailService
import com.glinboy.opportune.service.ProfileService
import com.glinboy.opportune.service.SessionService
import com.glinboy.opportune.service.VerificationCodeService
import com.glinboy.opportune.util.UUIDBase64
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ProfileServiceImpl(
	profileRepository: ProfileRepository,
	mapper: ProfileMapper,
	private val jwtTokenService: JwtTokenService,
	private val jwtDecoder: JwtDecoder,
	private val passwordEncoder: PasswordEncoder,
	private val verificationCodeService: VerificationCodeService,
	private val mailService: MailService,
	private val sessionService: SessionService,
	private val properties: ApplicationProperties
) :
	GenericServiceImpl<UUID, Profile, ProfileDTO, ProfileRepository,
		ProfileMapper>(profileRepository, mapper), ProfileService {

	override fun getCurrentProfile(): Optional<ProfileDTO> =
		repository
			.findById(SecurityUtils.getCurrentUserLoginID())
			.map(mapper::toDto)

	override fun loadUserByUsername(username: String): UserDetails =
		repository.findById(UUID.fromString(username))
			.map { UserSecurityDTO(it) }
			.orElseThrow { UsernameNotFoundException("User not found") }

	@Transactional
	override fun register(profileDTO: ProfileDTO) {
		repository.findOneByEmailIgnoreCase(profileDTO.email!!.lowercase())
			.ifPresentOrElse(
				{ throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use") },
				{
					val profile = Profile(
						id = null,
						email = profileDTO.email.lowercase(),
						forename = profileDTO.forename,
						surname = profileDTO.surname,
						password = passwordEncoder.encode(profileDTO.password),
						jobTitle = profileDTO.jobTitle,
						location = profileDTO.location,
						avatar = null,
						emailVerification = false,
						lastLogin = null,
						status = AccountStatus.ACTIVE,
						subscription = null,
						roles = setOf(Role.ROLE_USER),
						resume = null
					)
					repository.save(profile)
					sendVerificationEmail(mapper.toDto(profile))
				})
	}

	override fun login(loginRequestDTO: LoginRequestDTO): AccessTokenResponseDTO {
		return repository.findOneByEmailIgnoreCase(loginRequestDTO.email)
			.filter { passwordEncoder.matches(loginRequestDTO.password, it.password) }
			.map(mapper::toDto)
			.map { jwtTokenService.generateTokens(it, loginRequestDTO.rememberMe) }
			.orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password") }
	}

	override fun refreshToken(refreshToken: String): AccessTokenResponseDTO {
		val jwt: Jwt = try {
			jwtDecoder.decode(refreshToken)
		} catch (e: Exception) {
			throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token")
		}
		if (jwt.getClaimAsString(SecurityUtils.TYPE_CLAIM) != SecurityUtils.TYPE_TOKEN_REFRESH) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is not a refresh token")
		}
		val profileId = UUID.fromString(jwt.subject)
		return repository.findById(profileId)
			.map(mapper::toDto)
			.map { jwtTokenService.refreshAccessToken(jwt, it) }
			.orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found") }
	}

	private fun sendVerificationEmail(profileDTO: ProfileDTO) {
		log.info("Sending verification email to ${profileDTO.email}")
		verificationCodeService.createEmailVerificationCode(profileDTO.id!!).let {
			mailService.sendActivationEmail(profileDTO, UUIDBase64.toBase64(it!!.id!!))
		}
	}

	@Transactional
	override fun confirmEmail(code: String) {
		val id: UUID = UUIDBase64.fromBase64(code)
		verificationCodeService.findByEmailVerification(id)
			.map { vk ->
				vk.createdDate?.let {
					if (it.plus(properties.security.validationCodeDuration.emailVerify)
							.isBefore(Date().toInstant())) {
						verificationCodeService.delete(id)
						throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification code has expired")
					}
				} ?: throw IllegalStateException("createdDate must not be null on a verified reset token")
				vk.profileId?.let { profileId ->
					repository.updateEmailVerification(profileId, true)
					verificationCodeService.deleteAllProfileEmailVerification(profileId)
				} ?: throw IllegalStateException("profileId must not be null on a verified email token")
			}
			.orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification code not found") }
	}

	@Transactional
	override fun initiatePasswordReset(passwordResetInitiationRequestDTO: PasswordResetInitiationRequestDTO) {
		repository.findOneByEmailIgnoreCaseAndStatus(passwordResetInitiationRequestDTO.email, AccountStatus.ACTIVE)
			.map { profile ->
				profile.id?.let { profileId ->
					verificationCodeService.createPasswordResetCode(profileId)?.let { vk ->
							vk.id?.let { id ->
								mailService.sendPasswordResetMail(mapper.toDto(profile),
									UUIDBase64.toBase64(id))
							} ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password reset code not found")
					} ?: throw IllegalStateException("Failed to create password reset code")
				} ?: throw IllegalStateException("profileId must not be null on an active profile")
			}
	}

	@Transactional
	override fun finalizePasswordReset(passwordResetFinalizationRequestDTO: PasswordResetFinalizationRequestDTO) {
		val id: UUID = UUIDBase64.fromBase64(passwordResetFinalizationRequestDTO.code)
		verificationCodeService.findByPasswordReset(id)
			.map { vk ->
				vk.createdDate?.let {
					if (it.plus(properties.security.validationCodeDuration.passwordReset)
						.isBefore(Date().toInstant())) {
						verificationCodeService.delete(id)
						throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification code has expired")
					}
				} ?: throw IllegalStateException("createdDate must not be null on a verified reset token")
				vk.profileId?.let { profileId ->
					repository.findById(profileId).map { profile ->
						passwordEncoder.encode(passwordResetFinalizationRequestDTO.newPassword)?.let { enc ->
							repository.updatePassword(profileId, enc)
							verificationCodeService.deleteAllProfilePasswordReset(profileId)
							sessionService.terminateAllSessionForProfile(profileId, RevocationReason.PASSWORD_RESET)
							mailService.sendPasswordResetSuccessEmail(mapper.toDto(profile))
						} ?: throw IllegalStateException("Failed to encode new password")
					}
				} ?: throw IllegalStateException("profileId must not be null on a verified reset token")
			}
			.orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification code not found") }
	}

	@Transactional
	override fun changePassword(passwordUpdateRequestDTO: PasswordUpdateRequestDTO) {
		val currentUserId = UUID.fromString(SecurityUtils.getCurrentUserLogin())
		SecurityUtils.getCurrentUserLogin()
			?.let { UUID::fromString }
			?.let { profileId ->
				repository.findOneByIdAndStatus(currentUserId, AccountStatus.ACTIVE)
					.filter {
						passwordEncoder.matches(passwordUpdateRequestDTO.currentPassword, it.password)
					}
					.map { profile ->
						if(passwordEncoder.matches(passwordUpdateRequestDTO.newPassword, profile.password)) {
							throw ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from the current password")
						}
						repository.updatePassword(profile.id!!,
							passwordEncoder.encode(passwordUpdateRequestDTO.newPassword)!!)
						sessionService.terminateAllOtherSessions(RevocationReason.PASSWORD_CHANGE)
						mailService.sendPasswordResetSuccessEmail(mapper.toDto(profile))
					}
					.orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect") }
			}
	}

	override fun currentUserSpecification(): Specification<Profile> =
		createCurrentUserSpecification { it.get("id") }

	override fun validateOwnership(profileDTO: ProfileDTO) {
		if (profileDTO.id != null && profileDTO.id != UUID.fromString(SecurityUtils.getCurrentUserLogin())) {
			throw ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"You do not have permission to access this resource"
			)
		}
	}
}
