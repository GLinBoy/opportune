package com.glinboy.opportune.dto

import java.time.Instant
import java.util.*

data class EducationDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val school: String? = null,
	val degree: String? = null,
	val fieldOfStudy: String? = null,
	val startYear: Short? = null,
	val endYear: Short? = null,
	val isCurrent: Boolean = false,
	val gpa: String? = null,
	val honors: String? = null,
	val displayOrder: Int = 0,
	val profileId: UUID? = null,
	val courses: List<String> = emptyList()
) : AuditableDTO()
