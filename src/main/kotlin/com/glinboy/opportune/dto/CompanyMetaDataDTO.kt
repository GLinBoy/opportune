package com.glinboy.opportune.dto

import java.util.*

data class CompanyMetaDataDTO(
	override val id: UUID? = null,
	override val metaName: String? = null,
	override val metaValue: String? = null,
	val companyId: UUID? = null
) : MetaDataDTO()
