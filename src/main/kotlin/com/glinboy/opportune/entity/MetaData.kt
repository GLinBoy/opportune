package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "meta_data")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class MetaData(

	override val id: UUID? = null,

	@Column(name = "meta_name")
	val metaName: String? = null,

	@Lob
	@Column(name = "meta_value")
	val metaValue: String? = null
) : BaseEntity()
