package com.glinboy.opportune.entity

import com.glinboy.opportune.enums.VerificationCodeType
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "verification_code")
data class VerificationCode(
	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	val type: VerificationCodeType? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", nullable = false)
	val profile: Profile? = null,
): AuditableEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is VerificationCode) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = javaClass.hashCode()
}
