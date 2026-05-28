package com.glinboy.opportune.dto

import java.time.Instant
import java.util.UUID

data class AiQueueItemDTO(
    val id: UUID,
    val title: String?,
    val companyName: String?,
    val createdDate: Instant,
    val waitMinutes: Long
)
