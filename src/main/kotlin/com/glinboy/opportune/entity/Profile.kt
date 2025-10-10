package com.glinboy.opportune.entity

import com.glinboy.opportune.enums.AccountStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "profile")
data class Profile(

	override val id: UUID? = null,

	@Column(name = "email")
	val email: String? = null,

	@Column(name = "forename")
	val forename: String? = null,

	@Column(name = "surname")
	val surname: String? = null,

	@Column(name = "password")
	val password: String? = null,

	@Column(name = "job_title")
	val jobTitle: String? = null,

	@Column(name = "location")
	val location: String? = null,

	@Column(name = "avatar")
	val avatar: String? = null,

	@Column(name = "email_verification")
	val emailVerification: Boolean? = null,

	@Column(name = "last_login")
	val lastLogin: Instant? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	val status: AccountStatus? = null,

	@Column(name = "subscription")
	val subscription: String? = null,

	@OneToOne(mappedBy = "profile", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
	val resume: ProfileResume? = null,

	@OneToMany(mappedBy = "profile", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
	val applications: Set<Application> = emptySet(),

	@OneToMany(mappedBy = "profile", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
	val companies: Set<Company> = emptySet(),
) : BaseEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Profile) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
