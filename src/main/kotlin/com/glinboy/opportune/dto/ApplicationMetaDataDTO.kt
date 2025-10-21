package com.glinboy.opportune.dto

import java.time.Instant
import java.util.*

class ApplicationMetaDataDTO(
	override val id: UUID? = null,
	override val metaName: String? = null,
	override val metaValue: String? = null,
	val applicationId: UUID? = null,
		override val createdDate: Instant? = null,
	override val lastModifiedDate: Instant? = null
) : MetaDataDTO()
