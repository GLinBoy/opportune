package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "resume_publication")
data class ResumePublication(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "title", nullable = false)
	val title: String? = null,

	@Column(name = "publisher")
	val publisher: String? = null,

	@Column(name = "publication_date")
	val publicationDate: LocalDate? = null,

	@Column(name = "url")
	val url: String? = null,

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
		if (other !is ResumePublication) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
