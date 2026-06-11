package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "resume_certification")
data class ResumeCertification(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "name", nullable = false)
	val name: String? = null,

	@Column(name = "issuing_organization")
	val issuingOrganization: String? = null,

	@Column(name = "issue_date")
	val issueDate: LocalDate? = null,

	@Column(name = "expiration_date")
	val expirationDate: LocalDate? = null,

	@Column(name = "credential_id")
	val credentialId: String? = null,

	@Column(name = "credential_url")
	val credentialUrl: String? = null,

	@Column(name = "display_order", nullable = false)
	val displayOrder: Int = 0,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", nullable = false)
	val profile: Profile? = null
) : AuditableEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ResumeCertification) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
