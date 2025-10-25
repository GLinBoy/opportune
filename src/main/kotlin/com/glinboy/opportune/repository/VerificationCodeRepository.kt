package com.glinboy.opportune.repository

import com.glinboy.opportune.entity.VerificationCode
import com.glinboy.opportune.enums.VerificationCodeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VerificationCodeRepository : JpaRepository<VerificationCode, UUID>, JpaSpecificationExecutor<VerificationCode> {
	fun findByIdAndType(id: UUID, emailVerification: VerificationCodeType): Optional<VerificationCode>

	@Modifying
	fun deleteAllByProfileIdAndType(profileId: UUID, type: VerificationCodeType)
}

