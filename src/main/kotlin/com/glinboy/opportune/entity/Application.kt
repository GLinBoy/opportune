package com.glinboy.opportune.entity

import com.glinboy.opportune.enums.ApplicationStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "application")
data class Application(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "url")
	val url: String? = null,

	@Column(name = "title")
	val title: String? = null,

	@Column(name = "location")
	val location: String? = null,

	@Column(name = "applied_at")
	val appliedAt: Instant? = null,

	@Column(name = "salary")
	val salary: String? = null,

	@Lob
	@Column(name = "note")
	val note: String? = null,

	@Lob
	@Column(name = "raw_content")
	val rawContent: String? = null,

	@Lob
	@Column(name = "description")
	val description: String? = null,

	@Lob
	@Column(name = "cover_letter")
	val coverLetter: String? = null,

	@Lob
	@Column(name = "resume_insights")
	val resumeInsights: String? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	val status: ApplicationStatus? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	val company: Company? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id")
	val profile: Profile? = null,

	@OneToMany(mappedBy = "application", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
	val timeline: Set<ApplicationTimeline> = emptySet(),

	@OneToMany(mappedBy = "application", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
	val interviewNotes: Set<InterviewNote> = emptySet(),

	@OneToMany(mappedBy = "application", cascade = [CascadeType.ALL], orphanRemoval = true)
	val metadata: Set<ApplicationMetaData> = emptySet(),

	@OneToOne(mappedBy = "application", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
	val resume: ApplicationResume? = null,

	@OneToMany(mappedBy = "application", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
	val attachments: Set<ApplicationAttachment> = emptySet()
) : AuditableEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Application) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
