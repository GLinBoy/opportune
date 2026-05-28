package com.glinboy.opportune.projection

interface AiQueueProjection {
	fun getId(): java.util.UUID
	fun getTitle(): String?
	fun getCompanyName(): String?
	fun getCreatedDate(): java.time.Instant
}
