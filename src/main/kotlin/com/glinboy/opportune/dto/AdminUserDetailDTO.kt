package com.glinboy.opportune.dto

data class AdminUserDetailDTO(
	val profile: ProfileDTO,
	val applicationCount: Long = 0,
	val sessions: List<SessionDTO> = emptyList(),
)
