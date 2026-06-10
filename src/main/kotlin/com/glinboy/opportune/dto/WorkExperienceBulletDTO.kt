package com.glinboy.opportune.dto

import java.time.Instant
import java.util.*

data class WorkExperienceBulletDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val content: String? = null,
	val displayOrder: Int = 0
) : AuditableDTO()
