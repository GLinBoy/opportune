package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "interview_attachment")
class InterviewAttachment(
	id: UUID? = null,
	name: String? = null,
	path: String? = null,
	contentType: String? = null,
	contentLength: Long? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interview_note_id")
	val interviewNote: InterviewNote? = null,
) : Attachment(id, name, path, contentType, contentLength)
