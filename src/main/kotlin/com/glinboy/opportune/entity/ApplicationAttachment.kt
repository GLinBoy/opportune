package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "application_attachment")
class ApplicationAttachment(
	id: UUID? = null,
	name: String? = null,
	path: String? = null,
	contentType: String? = null,
	contentLength: Long? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	val application: Application? = null,
) : Attachment(id, name, path, contentType, contentLength)
