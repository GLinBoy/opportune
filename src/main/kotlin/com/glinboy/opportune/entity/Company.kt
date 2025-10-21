package com.glinboy.opportune.entity

import com.glinboy.opportune.enums.CompanyStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "company")
data class Company(

	override val id: UUID? = null,
	override val createdDate: Instant = Instant.now(),
	override val lastModifiedDate: Instant? = null,

	@Column(name = "name")
	val name: String? = null,

	@Column(name = "industry")
	val industry: String? = null,

	@Column(name = "website")
	val website: String? = null,

	@Column(name = "company_size")
	val companySize: String? = null,

	@Column(name = "location")
	val location: String? = null,

	@Column(name = "founded_year")
	val foundedYear: String? = null,

	@Lob
	@Column(name = "description")
	val description: String? = null,

	@Lob
	@Column(name = "note")
	val note: String? = null,

	@Column(name = "logo")
	val logo: String? = null,

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	val status: CompanyStatus? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id")
	val profile: Profile? = null,

	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
	val applications: Set<Application> = emptySet(),

	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
	val metaData: Set<CompanyMetaData> = emptySet()
) : AuditableEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Company) return false
		return id != null && id == other.id
	}

	override fun hashCode(): Int = id?.hashCode() ?: 0
}
