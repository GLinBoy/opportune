package com.glinboy.opportune.entity.id

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Embeddable
data class CompanyMetaDataId(
	@Column(name = "company_id")
	val companyId: UUID? = null,

	@Column(name = "meta_name")
	val metaName: String? = null
) : Serializable

