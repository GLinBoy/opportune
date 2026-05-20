package com.glinboy.opportune.projection

import com.glinboy.opportune.enums.ApplicationStatus

interface ApplicationStatProjection: ProjectionBase {
	fun getCreatedDay(): java.time.LocalDate
	fun getStatus(): ApplicationStatus
	fun getTotal(): Long
}
