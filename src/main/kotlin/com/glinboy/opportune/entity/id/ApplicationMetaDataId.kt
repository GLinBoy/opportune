package com.glinboy.opportune.entity.id

import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Embeddable
data class ApplicationMetaDataId(
	@Column(name = "application_id")
	val applicationId: UUID? = null,

	@Column(name = "meta_name")
	val metaName: String? = null
) : Serializable

