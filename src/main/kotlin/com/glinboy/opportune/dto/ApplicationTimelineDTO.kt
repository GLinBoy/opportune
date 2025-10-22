package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.ApplicationStatus
import java.time.Instant
import java.util.*

data class ApplicationTimelineDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val title: String? = null,
	val description: String? = null,
	val status: ApplicationStatus? = null,
	val occurredAt: Instant? = null,
	val applicationId: UUID? = null
) : AuditableDTO()

