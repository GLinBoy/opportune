package com.glinboy.opportune.dto

import java.util.*

data class SearchResultDTO(
	val id: UUID,
	val name: String,
	val status: String,
	val type: String
)
