package com.glinboy.opportune.projection

import com.glinboy.opportune.enums.ApplicationStatus
import java.time.Instant
import java.util.*

interface ApplicationProjection: ProjectionBase {
	fun getId(): UUID?
	fun getUrl(): String?
	fun getTitle(): String?
	fun getLocation(): String?
	fun getAppliedAt(): Instant?
	fun getSalary(): String?
	fun getNote(): String?
	fun getStatus(): ApplicationStatus?
	fun getCompanyId(): UUID?
	fun getCompanyName(): String?
	fun getCreatedDate(): Instant?
	fun getLastModifiedDate(): Instant?
}
