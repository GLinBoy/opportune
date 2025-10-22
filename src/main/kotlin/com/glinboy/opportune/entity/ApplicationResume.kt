package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "application_resume")
class ApplicationResume(
	id: UUID? = null,
	createdDate: Instant = Instant.now(),
	lastModifiedDate: Instant? = null,
	name: String? = null,
	path: String? = null,
	contentType: String? = null,
	contentLength: Long? = null,

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id", unique = true)
	val application: Application? = null
) : Attachment(id, createdDate, lastModifiedDate, name, path, contentType, contentLength)
