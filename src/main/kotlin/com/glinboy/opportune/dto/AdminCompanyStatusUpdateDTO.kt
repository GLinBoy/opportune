package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.CompanyStatus
import jakarta.validation.constraints.NotNull

data class AdminCompanyStatusUpdateDTO(
	@field:NotNull(message = "Status must not be null")
	val status: CompanyStatus? = null,
)
