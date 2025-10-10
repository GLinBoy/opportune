package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "interview_note")
data class InterviewNote(

	override val id: UUID? = null,

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
) : BaseEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is InterviewNote) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
