package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "attachment")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class Attachment(
	@Id
	val id: UUID = UUID.randomUUID(),

	@Column(name = "name")
	val name: String? = null,

	@Column(name = "path")
	val path: String? = null,

	@Column(name = "content_type")
	val contentType: String? = null,

	@Column(name = "content_length")
	val contentLength: Long? = null
)
