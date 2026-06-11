package com.glinboy.opportune.dto

import java.time.Instant
import java.time.LocalDate
import java.util.*

data class ResumeAwardDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val title: String? = null,
	val issuer: String? = null,
	val awardDate: LocalDate? = null,
	val description: String? = null,
	val displayOrder: Int = 0,
	val profileId: UUID? = null
) : AuditableDTO()
