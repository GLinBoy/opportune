package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "attachment")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class Attachment(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "name")
	val name: String? = null,

	@Column(name = "path")
	val path: String? = null,

	@Column(name = "content_type")
	val contentType: String? = null,

	@Column(name = "content_length")
	val contentLength: Long? = null
) : AuditableEntity()
