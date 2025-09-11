package com.glinboy.opportune.dto

import java.util.*

/**
 * DTO for ApplicationMetaData entity.
 * Uses composite ID (applicationId + metaName) via ApplicationMetaDataIdDTO.
 */
class ApplicationMetaDataDTO(
	override val id: UUID? = null,
	override val metaName: String? = null,
	override val metaValue: String? = null,
	val applicationId: UUID? = null
) : MetaDataDTO()
