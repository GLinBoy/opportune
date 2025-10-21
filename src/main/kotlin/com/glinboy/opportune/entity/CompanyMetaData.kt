package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "company_meta_data")
class CompanyMetaData(
	id: UUID? = null,
	createdDate: Instant = Instant.now(),
	lastModifiedDate: Instant? = null,
	metaName: String? = null,
	metaValue: String? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	val company: Company? = null
) : MetaData(id, createdDate, lastModifiedDate, metaName, metaValue)
