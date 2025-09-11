package com.glinboy.opportune.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.*

@MappedSuperclass
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
abstract class BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	val id: UUID? = null
}
