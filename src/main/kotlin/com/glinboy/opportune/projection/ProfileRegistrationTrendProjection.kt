package com.glinboy.opportune.projection

import java.time.LocalDate

interface ProfileRegistrationTrendProjection {
	fun getRegistrationDate(): LocalDate
	fun getTotal(): Long
}
