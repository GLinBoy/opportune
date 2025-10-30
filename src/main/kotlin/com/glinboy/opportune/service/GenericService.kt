package com.glinboy.opportune.service

import com.glinboy.opportune.dto.BaseDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import java.util.*

interface GenericService<ID, D : BaseDTO> {
	fun save(d: D): D

	fun saveAll(entities: List<D>): List<D>

	fun getById(id: ID): D

	fun getByIdForCurrentUser(id: ID): D

	fun findById(id: ID): Optional<D>

	fun findByIdForCurrentUser(id: ID): Optional<D>

	fun findAll(pageable: Pageable): Page<D>

	fun findAllForCurrentUser(pageable: Pageable): Page<D>

	fun findAll(specification: Specification<Any>, pageable: Pageable): Page<D>

	fun findAllForCurrentUser(specification: Specification<Any>, pageable: Pageable): Page<D>

	fun update(d: D): D

	fun updateForCurrentUser(d: D): D

	fun delete(id: ID): Unit

	fun deleteForCurrentUser(id: ID): Unit

	fun deleteAllByIds(ids: List<ID>): Unit

	fun deleteAllByIdsForCurrentUser(ids: List<ID>): Unit

	fun existsById(id: ID): Boolean

	fun existsByIdForCurrentUser(id: ID): Boolean
}
