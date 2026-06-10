package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "work_experience")
data class WorkExperience(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "job_title", nullable = false)
	val jobTitle: String? = null,

	@Column(name = "company", nullable = false)
	val company: String? = null,

	@Column(name = "location")
	val location: String? = null,

	@Column(name = "start_month")
	val startMonth: Short? = null,

	@Column(name = "start_year")
	val startYear: Short? = null,

	@Column(name = "end_month")
	val endMonth: Short? = null,

	@Column(name = "end_year")
	val endYear: Short? = null,

	@Column(name = "is_current", nullable = false)
	val isCurrent: Boolean = false,

	@Column(name = "display_order", nullable = false)
	val displayOrder: Int = 0,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", nullable = false)
	val profile: Profile? = null,

	@OneToMany(mappedBy = "workExperience", fetch = FetchType.LAZY)
	@OrderBy("display_order ASC")
	val bullets: Set<WorkExperienceBullet> = emptySet()
) : AuditableEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is WorkExperience) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
