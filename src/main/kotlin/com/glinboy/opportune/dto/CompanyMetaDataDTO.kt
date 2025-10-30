package com.glinboy.opportune.dto

import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.*

data class CompanyMetaDataDTO(
	override val id: UUID? = null,
	override val metaName: String? = null,
	override val metaValue: String? = null,
	@field:NotNull(message = "Company ID must not be null")
	val companyId: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null
) : MetaDataDTO()
