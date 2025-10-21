package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.CompanyStatus
import java.time.Instant
import java.util.*

data class CompanyDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val name: String? = null,
	val industry: String? = null,
	val website: String? = null,
	val companySize: String? = null,
	val location: String? = null,
	val foundedYear: String? = null,
	val description: String? = null,
	val note: String? = null,
	val logo: String? = null,
	val status: CompanyStatus? = null,
	val profileId: UUID? = null,
) : AuditableDTO()
