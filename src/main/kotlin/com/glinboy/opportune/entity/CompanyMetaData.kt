package com.glinboy.opportune.entity

import com.glinboy.opportune.entity.id.CompanyMetaDataId
import jakarta.persistence.*

@Entity
@Table(name = "company_meta_data")
data class CompanyMetaData(
	@EmbeddedId
	val id: CompanyMetaDataId = CompanyMetaDataId(),

	@Lob
	@Column(name = "meta_value")
	val metaValue: String? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", insertable = false, updatable = false)
	val company: Company? = null
) {
	// Convenience property to access metaName directly
	val metaName: String?
		get() = id.metaName
}

