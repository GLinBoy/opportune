package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "resume_project")
data class ResumeProject(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "name", nullable = false)
	val name: String? = null,

	@Column(name = "description")
	val description: String? = null,

	@Column(name = "url")
	val url: String? = null,

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

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "resume_project_tech", joinColumns = [JoinColumn(name = "resume_project_id")])
	@Column(name = "technology")
	val techStack: MutableList<String> = mutableListOf()
) : AuditableEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ResumeProject) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
