package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.ApplicationStatus
import java.time.Instant
import java.util.*

data class ApplicationDetailsDTO(
	override val id: UUID? = null,
	val url: String? = null,
	val title: String? = null,
	val location: String? = null,
	val appliedAt: Instant? = null,
	val salary: String? = null,
	val note: String? = null,
	val rawContent: String? = null,
	val description: String? = null,
	val coverLetter: String? = null,
	val resumeInsights: String? = null,
	val status: ApplicationStatus? = null,
	val company: CompanyDTO? = null,
	val timeline: Set<ApplicationTimelineDTO>? = null,
	val interviewNotes: Set<InterviewNoteDTO>? = null,
	val metadata: Set<ApplicationMetaDataDTO>? = null,
	val resume: ApplicationResumeDTO? = null,
	val attachments: Set<ApplicationAttachmentDTO>? = null,
) : BaseDTO()
