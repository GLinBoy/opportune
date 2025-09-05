package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "interview_attachment")
class InterviewAttachment(
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interview_note_id")
	val interviewNote: InterviewNote? = null,

	id: UUID = UUID.randomUUID(),
	name: String? = null,
	path: String? = null,
	contentType: String? = null,
	contentLength: Long? = null
) : Attachment(id, name, path, contentType, contentLength)
