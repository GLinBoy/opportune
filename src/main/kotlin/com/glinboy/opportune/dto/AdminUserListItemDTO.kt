package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.AccountStatus
import com.glinboy.opportune.enums.Role
import java.time.Instant
import java.util.*

data class AdminUserListItemDTO(
	val id: UUID? = null,
	val email: String? = null,
	val forename: String? = null,
	val surname: String? = null,
	val jobTitle: String? = null,
	val status: AccountStatus? = null,
	val roles: Set<Role> = emptySet(),
	val emailVerification: Boolean? = null,
	val lastLogin: Instant? = null,
	val createdDate: Instant? = null,
	val lastModifiedDate: Instant? = null,
	val avatar: String? = null,
)
