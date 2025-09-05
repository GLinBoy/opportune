package com.glinboy.opportune.entity

import com.glinboy.opportune.enums.CompanyStatus
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "company")
data class Company(
	@Id
	val id: UUID = UUID.randomUUID(),

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

	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
	val applications: Set<Application> = emptySet(),

	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
	val metaData: Set<CompanyMetaData> = emptySet()
)
