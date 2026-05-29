package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.CompanyStatus

data class AdminCompanyUpdateDTO(
	val name: String? = null,
	val industry: String? = null,
	val website: String? = null,
	val companySize: String? = null,
	val location: String? = null,
	val foundedYear: String? = null,
	val description: String? = null,
	val note: String? = null,
	val status: CompanyStatus? = null,
)
