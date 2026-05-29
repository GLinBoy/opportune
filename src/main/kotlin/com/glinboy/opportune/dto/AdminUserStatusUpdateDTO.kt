package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.AccountStatus
import jakarta.validation.constraints.NotNull

data class AdminUserStatusUpdateDTO(
	@field:NotNull(message = "Status must not be null")
	val status: AccountStatus? = null,
)
