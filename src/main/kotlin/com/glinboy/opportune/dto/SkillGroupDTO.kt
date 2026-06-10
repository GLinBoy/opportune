package com.glinboy.opportune.dto

import java.time.Instant
import java.util.*

data class SkillGroupDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val category: String? = null,
	val displayOrder: Int = 0,
	val profileId: UUID? = null,
	val skills: List<String> = emptyList()
) : AuditableDTO()
