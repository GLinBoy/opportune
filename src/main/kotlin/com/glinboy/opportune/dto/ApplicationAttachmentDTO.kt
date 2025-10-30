package com.glinboy.opportune.dto

import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.*

data class ApplicationAttachmentDTO(
	override val id: UUID? = null,
	override val name: String? = null,
	override val path: String? = null,
	override val contentType: String? = null,
	override val contentLength: Long? = null,
	@field:NotNull(message = "Application ID must not be null")
	val applicationId: UUID? = null,
	override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null
) : AttachmentDTO()
