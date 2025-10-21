package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.Role
import java.time.Instant
import java.util.*

data class ProfileDTO(
	override val id: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null,
	val email: String? = null,
	val forename: String? = null,
	val surname: String? = null,
	val password: String? = null,
	val jobTitle: String? = null,
	val location: String? = null,
	val avatar: String? = null,
	val emailVerification: Boolean? = null,
	val lastLogin: Instant? = null,
	val status: AccountStatus? = null,
	val subscription: String? = null,
	val roles: Set<Role> = emptySet(),
	val resumeId: UUID? = null
) : AuditableDTO()
