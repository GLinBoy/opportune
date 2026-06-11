package com.glinboy.opportune.dto

import java.time.Instant
import java.time.LocalDate
import java.util.*

data class ResumePublicationDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val title: String? = null,
	val publisher: String? = null,
	val publicationDate: LocalDate? = null,
	val url: String? = null,
	val description: String? = null,
	val displayOrder: Int = 0,
	val profileId: UUID? = null
) : AuditableDTO()
