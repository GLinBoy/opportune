package com.glinboy.opportune.projection

import com.glinboy.opportune.enums.AccountStatus

interface ProfileStatusCountProjection {
	fun getStatus(): AccountStatus
	fun getTotal(): Long
}
