package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "volunteer_work")
data class VolunteerWork(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "role", nullable = false)
	val role: String? = null,

	@Column(name = "organization", nullable = false)
	val organization: String? = null,

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

	@Column(name = "description")
	val description: String? = null,

	@Column(name = "display_order", nullable = false)
	val displayOrder: Int = 0,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", nullable = false)
	val profile: Profile? = null
) : AuditableEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is VolunteerWork) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
