package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "interview_note")
data class InterviewNote(
	@Id
	val id: UUID = UUID.randomUUID(),

	@Column(name = "date")
	val date: Instant? = null,

	@Lob
	@Column(name = "notes")
	val notes: String? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	val application: Application? = null,

	@OneToMany(mappedBy = "interviewNote", cascade = [CascadeType.ALL], orphanRemoval = true)
	val attachments: List<InterviewAttachment> = emptyList()
)

