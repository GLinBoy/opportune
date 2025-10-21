package com.glinboy.opportune.dto

import java.time.Instant
import java.util.*

data class ProfileResumeDTO(
	override val id: UUID? = null,
	override val name: String? = null,
	override val path: String? = null,
	override val contentType: String? = null,
	override val contentLength: Long? = null,
	val profileId: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null
) : AttachmentDTO()
