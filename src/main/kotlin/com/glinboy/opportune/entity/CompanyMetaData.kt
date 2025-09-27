package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "company_meta_data")
class CompanyMetaData(
	id: UUID? = null,
	metaName: String? = null,
	metaValue: String? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	val company: Company? = null
) : MetaData(id, metaName, metaValue)
