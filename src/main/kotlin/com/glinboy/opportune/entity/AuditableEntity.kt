package com.glinboy.opportune.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.time.Instant

@MappedSuperclass
abstract class AuditableEntity: BaseEntity() {

	@Column(name = "CREATED_DATE", updatable = false, nullable = false)
	open val createdDate: Instant = Instant.now()

	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	open val lastModifiedDate: Instant? = null
}
