package com.glinboy.opportune.entity

import com.glinboy.opportune.enums.ApplicationStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "application_timeline")
data class ApplicationTimeline(
	@Id
	val id: UUID = UUID.randomUUID(),

	@Column(name = "title")
	val title: String? = null,

	@Lob
	@Column(name = "description")
	val description: String? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	val status: ApplicationStatus? = null,

	@Column(name = "occurred_at")
	val occurredAt: Instant? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id")
	val application: Application? = null
)
