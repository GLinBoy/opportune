package com.glinboy.opportune.dto

import java.time.Instant
import java.util.*

data class WorkExperienceDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val jobTitle: String? = null,
	val company: String? = null,
	val location: String? = null,
	val startMonth: Short? = null,
	val startYear: Short? = null,
	val endMonth: Short? = null,
	val endYear: Short? = null,
	val isCurrent: Boolean = false,
	val displayOrder: Int = 0,
	val profileId: UUID? = null,
	val bullets: List<WorkExperienceBulletDTO> = emptyList()
) : AuditableDTO()
