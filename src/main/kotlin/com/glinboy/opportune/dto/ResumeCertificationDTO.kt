package com.glinboy.opportune.dto

import java.time.Instant
import java.time.LocalDate
import java.util.*

data class ResumeCertificationDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val name: String? = null,
	val issuingOrganization: String? = null,
	val issueDate: LocalDate? = null,
	val expirationDate: LocalDate? = null,
	val credentialId: String? = null,
	val credentialUrl: String? = null,
	val displayOrder: Int = 0,
	val profileId: UUID? = null
) : AuditableDTO()
