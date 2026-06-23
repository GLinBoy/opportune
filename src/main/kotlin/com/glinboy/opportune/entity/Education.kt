package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "education")
data class Education(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "school", nullable = false)
	val school: String? = null,

	@Column(name = "degree", nullable = false)
	val degree: String? = null,

	@Column(name = "field_of_study", nullable = false)
	val fieldOfStudy: String? = null,

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

	@Column(name = "gpa")
	val gpa: String? = null,

	@Column(name = "honors")
	val honors: String? = null,

	@Column(name = "display_order", nullable = false)
	val displayOrder: Int = 0,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", nullable = false)
	val profile: Profile? = null,

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "education_course", joinColumns = [JoinColumn(name = "education_id")])
	@Column(name = "course_name")
	val courses: MutableList<String> = mutableListOf()
) : AuditableEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Education) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
