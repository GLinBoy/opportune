package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "application_resume")
class ApplicationResume(
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id", unique = true)
	val application: Application? = null,

	id: UUID = UUID.randomUUID(),
	name: String? = null,
	path: String? = null,
	contentType: String? = null,
	contentLength: Long? = null
) : Attachment(id, name, path, contentType, contentLength)
