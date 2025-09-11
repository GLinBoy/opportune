package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "application_meta_data")
class ApplicationMetaData(
	id: UUID? = null,
	metaName: String? = null,
	metaValue: String? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	val application: Application? = null,
) : MetaData(id, metaName, metaValue)


