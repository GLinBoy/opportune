package com.glinboy.opportune.dto

import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.*

data class InterviewNoteDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val date: Instant? = null,
	val notes: String? = null,
	@field:NotNull(message = "Application ID must not be null")
	val applicationId: UUID? = null
) : AuditableDTO()

