package com.glinboy.opportune.dto

import java.time.Instant

abstract class AuditableDTO: BaseDTO() {
		open val createdDate: Instant? = null
		open val lastModifiedDate: Instant? = null
}
