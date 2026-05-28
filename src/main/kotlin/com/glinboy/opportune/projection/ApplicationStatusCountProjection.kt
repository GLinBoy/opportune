package com.glinboy.opportune.projection

interface ApplicationStatusCountProjection {
	fun getStatus(): com.glinboy.opportune.enums.ApplicationStatus
	fun getTotal(): Long
}
