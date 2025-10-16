package com.glinboy.opportune.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Auditable: BaseEntity() {

	@CreatedBy
	@Column(name = "CREATED_BY", updatable = false, nullable = false)
	val createdBy: String? = null

	@CreatedDate
	@Column(name = "CREATED_DATE", updatable = false, nullable = false)
	val createdDate: Long? = null

	@LastModifiedBy
	@Column(name = "LAST_MODIFIED_BY", nullable = false)
	val lastModifiedBy: String? = null

	@LastModifiedDate
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	val lastModifiedDate: Long? = null

	@Version
	@Column(name = "VERSION", nullable = false)
	val version: Long? = null
}
