package com.glinboy.opportune.dto

import com.glinboy.opportune.enums.Role
import jakarta.validation.constraints.NotNull

data class AdminUserRoleUpdateDTO(
	@field:NotNull(message = "Role must not be null")
	val role: Role? = null,
)
