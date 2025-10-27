package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.VerificationCodeType
import java.time.Instant
import java.util.*

data class VerificationCodeDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val type: VerificationCodeType? = null,
	val profileId: UUID? = null
) : AuditableDTO()
