package com.glinboy.opportune.dto

import java.util.*

data class ApplicationResumeDTO(
	override val id: UUID? = null,
	override val name: String? = null,
	override val path: String? = null,
	override val contentType: String? = null,
	override val contentLength: Long? = null,
	val applicationId: UUID? = null
) : AttachmentDTO()
