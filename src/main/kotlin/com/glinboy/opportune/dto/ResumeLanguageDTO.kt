package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.LanguageProficiency
import java.time.Instant
import java.util.*

data class ResumeLanguageDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val language: String? = null,
	val proficiency: LanguageProficiency? = null,
	val displayOrder: Int = 0,
	val profileId: UUID? = null
) : AuditableDTO()
