package com.glinboy.opportune.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.UuidGenerator
import java.util.*

@MappedSuperclass
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
abstract class BaseEntity {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	open val id: UUID? = null
}
