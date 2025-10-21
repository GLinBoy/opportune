package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "meta_data")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class MetaData(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "meta_name")
	val metaName: String? = null,

	@Lob
	@Column(name = "meta_value")
	val metaValue: String? = null
) : AuditableEntity()
