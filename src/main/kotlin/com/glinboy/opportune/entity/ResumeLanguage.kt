package com.glinboy.opportune.entity

import com.glinboy.opportune.enums.LanguageProficiency
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "resume_language")
data class ResumeLanguage(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "language", nullable = false)
	val language: String? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "proficiency", nullable = false)
	val proficiency: LanguageProficiency? = null,

	@Column(name = "display_order", nullable = false)
	val displayOrder: Int = 0,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", nullable = false)
	val profile: Profile? = null
) : AuditableEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ResumeLanguage) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
