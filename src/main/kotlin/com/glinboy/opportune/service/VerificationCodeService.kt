package com.glinboy.opportune.service

import com.glinboy.opportune.dto.VerificationCodeDTO
import java.util.*

interface VerificationCodeService : GenericService<UUID, VerificationCodeDTO> {
	fun createEmailVerificationCode(id: UUID): VerificationCodeDTO?
	fun findByEmailVerification(id: UUID): Optional<VerificationCodeDTO>
	fun deleteAllProfileEmailVerification(profileId: UUID)
	fun createPasswordResetCode(id: UUID): VerificationCodeDTO?
	fun findByPasswordReset(id: UUID): Optional<VerificationCodeDTO>
	fun deleteAllProfilePasswordReset(profileId: UUID)
}

