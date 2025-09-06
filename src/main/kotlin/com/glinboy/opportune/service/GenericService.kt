package com.glinboy.opportune.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface GenericService<T, ID> {
	fun save(t: T): T

	fun saveAll(entities: List<T>): List<T>

	fun findById(id: ID): Optional<T>

	fun getById(id: ID): T

	fun findAll(pageable: Pageable): Page<T>

	fun update(t: T): T

	fun delete(id: ID): Unit

	fun deleteAllByIds(ids: List<ID>): Unit

	fun existsById(id: ID): Boolean

	fun count(): Long
}
