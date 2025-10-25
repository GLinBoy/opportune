package com.glinboy.opportune.service.impl

import com.glinboy.opportune.dto.VerificationCodeDTO
import com.glinboy.opportune.entity.Profile
import com.glinboy.opportune.entity.VerificationCode
import com.glinboy.opportune.enums.VerificationCodeType
import com.glinboy.opportune.mapper.VerificationCodeMapper
import com.glinboy.opportune.repository.VerificationCodeRepository
import com.glinboy.opportune.service.VerificationCodeService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class VerificationCodeServiceImpl(verificationCodeRepository: VerificationCodeRepository, mapper: VerificationCodeMapper) :
	GenericServiceImpl<UUID, VerificationCode, VerificationCodeDTO, VerificationCodeRepository, VerificationCodeMapper>(verificationCodeRepository, mapper),
	VerificationCodeService {

	override fun createEmailVerificationKey(id: UUID): VerificationCodeDTO? {
		val verificationCode = VerificationCode(
			type = VerificationCodeType.EMAIL_VERIFICATION,
			profile = Profile(id = id)
		)
		return mapper.toDto(repository.save(verificationCode))
	}

	override fun findByEmailVerification(id: UUID): Optional<VerificationCodeDTO> =
		repository
			.findByIdAndType(id, VerificationCodeType.EMAIL_VERIFICATION)
			.map(mapper::toDto)

	@Transactional
	override fun deleteAllProfileEmailVerification(profileId: UUID) =
		repository.deleteAllByProfileIdAndType(profileId, VerificationCodeType.EMAIL_VERIFICATION)

}

