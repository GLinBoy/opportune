package com.glinboy.opportune.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.glinboy.opportune.enums.WebhookTokenStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "webhook_token")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class WebhookToken(
	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "endpoint_identifier", nullable = false, unique = true)
	val endpointIdentifier: String? = null,

	@Column(name = "token_hash", nullable = false, unique = true)
	val tokenHash: String? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	val status: WebhookTokenStatus = WebhookTokenStatus.ACTIVE,

	@Column(name = "last_used_at")
	val lastUsedAt: Instant? = null,

	@Column(name = "revoked_at")
	val revokedAt: Instant? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", nullable = false)
	val profile: Profile? = null
) : AuditableEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is WebhookToken) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
