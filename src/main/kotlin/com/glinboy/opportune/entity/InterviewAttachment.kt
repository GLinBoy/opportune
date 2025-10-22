package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "interview_attachment")
class InterviewAttachment(
	id: UUID? = null,
	createdDate: Instant = Instant.now(),
	lastModifiedDate: Instant? = null,
	name: String? = null,
	path: String? = null,
	contentType: String? = null,
	contentLength: Long? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interview_note_id")
	val interviewNote: InterviewNote? = null,
) : Attachment(id, createdDate, lastModifiedDate, name, path, contentType, contentLength)
