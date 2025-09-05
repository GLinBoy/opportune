package com.glinboy.opportune.entity

import com.glinboy.opportune.entity.id.ApplicationMetaDataId
import jakarta.persistence.*

@Entity
@Table(name = "application_meta_data")
data class ApplicationMetaData(
	@EmbeddedId
	val id: ApplicationMetaDataId = ApplicationMetaDataId(),

	@Lob
	@Column(name = "meta_value")
	val metaValue: String? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id", insertable = false, updatable = false)
	val application: Application? = null
) {
	// Convenience property to access metaName directly
	val metaName: String?
		get() = id.metaName
}


