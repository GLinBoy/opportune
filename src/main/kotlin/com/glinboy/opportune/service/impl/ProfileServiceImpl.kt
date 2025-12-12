package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.*
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.Role
import com.glinboy.opportune.mapper.ProfileMapper
import com.glinboy.opportune.repository.ProfileRepository
import com.glinboy.opportune.security.SecurityUtils
import com.glinboy.opportune.security.jwt.JwtService
import com.glinboy.opportune.service.MailService
import com.glinboy.opportune.service.ProfileService
import com.glinboy.opportune.service.VerificationCodeService
import com.glinboy.opportune.util.UUIDBase64
import jakarta.transaction.Transactional
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class ProfileServiceImpl(
	profileRepository: ProfileRepository,
	mapper: ProfileMapper,
	private val jwtService: JwtService,
	private val passwordEncoder: PasswordEncoder,
	private val verificationCodeService: VerificationCodeService,
	private val mailService: MailService
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
		// TODO: Implement refresh token generation
		return repository.findOneByEmailIgnoreCase(loginRequestDTO.email)
			.filter { passwordEncoder.matches(loginRequestDTO.password, it.password) }
			.map { jwtService.createToken(it, loginRequestDTO.rememberMe) }
			.orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password") }
	}

	private fun sendVerificationEmail(profileDTO: ProfileDTO) {
		log.info("Sending verification email to ${profileDTO.email}")
		verificationCodeService.createEmailVerificationCode(profileDTO.id!!).let {
			mailService.sendActivationEmail(profileDTO, UUIDBase64.toBase64(it!!.id!!))
		}
	}

	@Transactional
	override fun confirmEmail(code: String) {
		verificationCodeService.findByEmailVerification(UUIDBase64.fromBase64(code))
			.map { vk ->
				repository.updateEmailVerification(vk.profileId!!, true)
				verificationCodeService.deleteAllProfileEmailVerification(vk.profileId!!)
			}
			.orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification code not found") }
	}

	override fun initiatePasswordReset(passwordResetInitiationRequestDTO: PasswordResetInitiationRequestDTO) {
		repository.findOneByEmailIgnoreCase(passwordResetInitiationRequestDTO.email)
			.map { profile ->
				verificationCodeService.createPasswordResetCode(profile.id!!).let { vk ->
					mailService.sendPasswordResetMail(
						mapper.toDto(profile),
						UUIDBase64.toBase64(vk!!.id!!)
					)
				}
			}
	}

	@Transactional
	override fun finalizePasswordReset(passwordResetFinalizationRequestDTO: PasswordResetFinalizationRequestDTO) {
		verificationCodeService.findByPasswordReset(UUIDBase64.fromBase64(passwordResetFinalizationRequestDTO.code))
			.map { vk ->
				repository.findById(vk.profileId!!).map { profile ->
					repository.updatePassword(
						profile.id!!,
						passwordEncoder.encode(passwordResetFinalizationRequestDTO.newPassword)!!
					)
					verificationCodeService.deleteAllProfilePasswordReset(vk.profileId!!)
				}
			}
	}

	@Transactional
	override fun changePassword(passwordUpdateRequestDTO: PasswordUpdateRequestDTO) {
		val currentUserId = UUID.fromString(SecurityUtils.getCurrentUserLogin())
		repository.findById(currentUserId)
			.filter {
				passwordEncoder.matches(passwordUpdateRequestDTO.currentPassword, it.password)
			}
			.map { profile ->
				repository.updatePassword(
					profile.id!!,
					passwordEncoder.encode(passwordUpdateRequestDTO.newPassword)!!
				)
			}
			.orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect") }
	}

	override fun currentUserSpecification(): Specification<Profile> =
		createCurrentUserSpecification { it.get("id") }

	override fun validateOwnership(profileDTO: ProfileDTO) {
		if (profileDTO.id != null && profileDTO.id != UUID.fromString(SecurityUtils.getCurrentUserLogin())) {
			throw ResponseStatusException(HttpStatus.FORBIDDEN,
				"You do not have permission to access this resource")
		}
	}
}
