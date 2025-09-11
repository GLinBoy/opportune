package com.glinboy.opportune.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "profile_resume")
class ProfileResume(
	id: UUID = UUID.randomUUID(),
	name: String? = null,
	path: String? = null,
	contentType: String? = null,
	contentLength: Long? = null,

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", unique = true)
	val profile: Profile? = null
) : Attachment(id, name, path, contentType, contentLength)
