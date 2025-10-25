package com.glinboy.opportune.service

import com.glinboy.opportune.dto.VerificationCodeDTO
import java.util.*

interface VerificationCodeService : GenericService<UUID, VerificationCodeDTO> {
	fun createEmailVerificationKey(id: UUID): VerificationCodeDTO?
	fun findByEmailVerification(id: UUID): Optional<VerificationCodeDTO>
	fun deleteAllProfileEmailVerification(profileId: UUID)
}

