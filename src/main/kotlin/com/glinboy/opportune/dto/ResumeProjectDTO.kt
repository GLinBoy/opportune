package com.glinboy.opportune.dto

import java.time.Instant
import java.util.*

data class ResumeProjectDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val name: String? = null,
	val description: String? = null,
	val url: String? = null,
	val startMonth: Short? = null,
	val startYear: Short? = null,
	val endMonth: Short? = null,
	val endYear: Short? = null,
	val isCurrent: Boolean = false,
	val displayOrder: Int = 0,
	val profileId: UUID? = null,
	val techStack: List<String> = emptyList()
) : AuditableDTO()
